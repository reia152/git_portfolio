package com.example.pf1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pf1.constants.AccountsFormConstants;
import com.example.pf1.constants.AccountsValues;
import com.example.pf1.dto.ProfileEditForm;
import com.example.pf1.entity.Accounts;
import com.example.pf1.messages.ErrorMessages;
import com.example.pf1.messages.InfoMessages;
import com.example.pf1.repository.AccountsRepository;
import com.example.pf1.validator.AccountsValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final AccountsRepository accountsRepository;
    private final AccountsValidator accountsValidator;

    // 拡張子はサーバー側で許可リストと突き合わせる（クライアントが送ってきた文字列をそのまま
    // ファイルパスに使うと、細工されたファイル名で意図しない場所に書き込まれる恐れがあるため。
    // 「15-p1-account-add」のsaveProfileImageと同じ実装）
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${app.media.location}")
    private String mediaLocation;

    // GETリクエスト時（プロフィール編集フォームの初期表示）
    @GetMapping("/profile/edit")
    public String editProfileForm(@AuthenticationPrincipal Accounts user, Model model) {
        ProfileEditForm form = new ProfileEditForm();
        // 現在のユーザー情報をフォームの初期値として設定
        form.setUsername(user.getUsername());
        form.setFurigana(user.getFurigana());
        form.setGender(user.getGender());
        form.setAge(user.getAge());
        form.setProfile(user.getProfile());
        model.addAttribute("profileEditForm", form);
        model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
        model.addAttribute("currentUser", user);
        model.addAttribute("imageNotFound", false);
        return "pf1/user/profile_edit";
    }

    // POSTリクエスト時（プロフィールの保存）
    @PostMapping("/profile/edit")
    public String editProfile(
            @Valid @ModelAttribute("profileEditForm") ProfileEditForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal Accounts user,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "clearProfileImage", required = false) String clearProfileImage,
            RedirectAttributes redirectAttributes,
            Model model) {

        // ふりがなのバリデーション（「バリデーション：ふりがな」タスクで作成済み）
        if (form.getFurigana() != null && !form.getFurigana().isEmpty()) {
            String furiganaError = accountsValidator.validateFurigana(form.getFurigana());
            if (furiganaError != null) {
                bindingResult.rejectValue("furigana", "error", furiganaError);
            }
        }

        // 性別のバリデーション（`AccountsValidator.validateGender` は「ユーザーマスタ作成」タスクで作成済み）
        if (form.getGender() != null && !form.getGender().isEmpty()) {
            String genderError = accountsValidator.validateGender(form.getGender());
            if (genderError != null) {
                bindingResult.rejectValue("gender", "error", genderError);
            }
        }

        // ユーザー名重複チェック（「バリデーション：ユーザー名」タスクで作成済み）
        if (accountsRepository.existsByUsernameAndUserIdNot(form.getUsername(), user.getUserId())) {
            bindingResult.rejectValue("username", "error", ErrorMessages.ERROR_USERNAME_EXISTS);
        }

        // 画像サイズチェック（「プロフィール画像」タスクで作成済みの saveProfileImage/deleteProfileImage）
        // ※ ここではサイズの妥当性だけを見る。実際のファイル保存（saveProfileImage）は
        //   他の項目も含めた全バリデーションを通過した後（DB更新の直前）に行う。
        //   先にファイルへ書き込んでしまうと、他の項目でバリデーションエラーになった場合に
        //   保存済みの画像だけがどのレコードにも紐付かずディスクに残り続けてしまうため。
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageSizeError = accountsValidator.validateProfileImageSize(profileImage.getSize());
            if (imageSizeError != null) {
                bindingResult.rejectValue("profileImagePath", "error", imageSizeError);
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
            model.addAttribute("currentUser", user);
            model.addAttribute("imageNotFound", false);
            model.addAttribute("flashMessage", ErrorMessages.ERROR_EDIT_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/profile_edit";
        }

        try {
            // @AuthenticationPrincipalのuserはログイン時点のスナップショット（セッションに保持されたdetachedな
            // JPAエンティティ）で、以降の更新は反映されない。これをそのままsave()するとmergeとして働き、
            // 他の全カラムがログイン時点の値で上書きされてしまう。そのため必ずDBから最新のエンティティを
            // 取り直し、そちらだけを書き換えて保存する。
            Accounts current = accountsRepository.findById(user.getUserId()).orElseThrow();

            // 「プロフィール画像を削除」チェックがある場合、画像ファイルを削除
            if ("true".equals(clearProfileImage) && current.getProfileImage() != null) {
                deleteProfileImage(current.getProfileImage());
                current.setProfileImage(null);
            }

            // 新しい画像がアップロードされていれば、ここで初めてファイルに保存する。
            // 差し替え前の旧ファイルは、DBのパスを上書きするだけでは物理的に残り続けてしまうため、
            // 保存後にdeleteProfileImageで削除する
            if (profileImage != null && !profileImage.isEmpty()) {
                String oldImage = current.getProfileImage();
                String newImagePath = saveProfileImage(profileImage);
                current.setProfileImage(newImagePath);
                if (oldImage != null) {
                    deleteProfileImage(oldImage);
                }
            }

            // ユーザー情報を更新して保存
            current.setUsername(form.getUsername());
            current.setFurigana(form.getFurigana());
            current.setGender(form.getGender());
            current.setAge(form.getAge());
            current.setProfile(form.getProfile());
            accountsRepository.save(current);

            redirectAttributes.addFlashAttribute("flashMessage", InfoMessages.INFO_EDIT_SUCCESS);
            redirectAttributes.addFlashAttribute("flashType", "success");
            return "redirect:/profile/edit";

        } catch (Exception e) {
            log.error("プロフィールの保存に失敗しました", e);
            model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
            model.addAttribute("currentUser", user);
            model.addAttribute("imageNotFound", false);
            model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/profile_edit";
        }
    }
    
 // プロフィール詳細（ログイン不要）
 // @PathVariable：URLのパスパラメータを受け取る
 @GetMapping("/profile/detail/{userId}")
 public String detailProfile(@PathVariable Long userId, Model model) {
     // 指定された userId で削除されていないユーザーを取得（存在しない場合は404）
     Accounts user = accountsRepository.findById(userId)
         .filter(u -> u.getIsDeleted() == 0)
         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

     // 性別コードを表示名に変換
     String genderDisplay = AccountsFormConstants.GENDER_LIST.stream()
         .filter(g -> g[0].equals(user.getGender()))
         .map(g -> g[1])
         .findFirst()
         .orElse("-");

     model.addAttribute("requestUser", user);
     model.addAttribute("genderDisplay", genderDisplay);
     return "pf1/public/profile_detail";
 }

//アカウント一覧（ログイン不要）
@GetMapping("/profile/list")
public String listProfile(Model model) {
  // is_deleted=0のレコードをフィルタリングし、usernameでソートして取得
  model.addAttribute("users", accountsRepository.findByIsDeletedOrderByUsername(AccountsValues.IS_DELETED_FALSE));
  return "pf1/public/profile_list";
}

    // プロフィール画像をファイルシステムに保存し、相対パスを返す
    private String saveProfileImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            String candidate = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (ALLOWED_IMAGE_EXTENSIONS.contains(candidate)) {
                extension = candidate;
            }
        }
        String filename = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get(mediaLocation, "profile_images");
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath);

        return "profile_images/" + filename;
    }

    // プロフィール画像の物理ファイルを削除する
    private void deleteProfileImage(String imagePath) {
        try {
            Path filePath = Paths.get(mediaLocation, imagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 削除に失敗しても処理は継続する（deleteIfExists はファイルが存在しない場合は
            // 例外を投げずfalseを返すだけなので、ここで捕捉するのは権限エラー等のI/O障害時のみ）
        }
    }
}