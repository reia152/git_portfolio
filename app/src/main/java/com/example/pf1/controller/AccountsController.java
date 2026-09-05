package com.example.pf1.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pf1.constants.AccountsFormConstants;
import com.example.pf1.dto.RegistrationForm;
import com.example.pf1.dto.SettingsForm;
import com.example.pf1.entity.Accounts;
import com.example.pf1.messages.ErrorMessages;
import com.example.pf1.messages.InfoMessages;
import com.example.pf1.repository.AccountsRepository;
import com.example.pf1.service.AccountsService;
import com.example.pf1.validator.AccountsValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountsController {

    private final AccountsRepository accountsRepository;
    private final AccountsService accountsService;
    private final AccountsValidator accountsValidator;

    // GETリクエスト時（設定フォームの初期表示）
    @GetMapping("/settings")
    public String settingsForm(@AuthenticationPrincipal Accounts user, Model model) {
        SettingsForm form = new SettingsForm();
        form.setUsername(user.getUsername());  // 現在の値を初期表示
        form.setEmail(user.getEmail());
        form.setPassword("");
        model.addAttribute("settingsForm", form);
        return "pf1/user/accounts_setting";
    }

    // POSTリクエスト時（設定の保存）
    @PostMapping("/settings")
    public String settingsUpdate(
            @Valid @ModelAttribute("settingsForm") SettingsForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal Accounts user,
            RedirectAttributes redirectAttributes,
            Model model) {

        // パスワードの長さチェック（任意入力なので空なら検証しない）
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            String passwordError = accountsValidator.validatePassword(form.getPassword());
            if (passwordError != null) {
                bindingResult.rejectValue("password", "error", passwordError);
            }
        }

        // ユーザー名の重複チェック（自分自身を除外。username は @NotBlank のため null/空文字は考慮不要）
        if (accountsRepository.existsByUsernameAndUserIdNot(form.getUsername(), user.getUserId())) {
            bindingResult.rejectValue("username", "error", ErrorMessages.ERROR_USERNAME_EXISTS);
        }

        // メールアドレスの重複チェック（自分自身を除外。email も @NotBlank のため同様）
        if (accountsRepository.existsByEmailAndUserIdNot(form.getEmail(), user.getUserId())) {
            bindingResult.rejectValue("email", "error", ErrorMessages.ERROR_EMAIL_EXISTS);
        }

        if (bindingResult.hasErrors()) {
            form.setPassword("");  // 画面再表示時に入力済みパスワードを平文のまま残さない
            model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_setting";  // エラー時はフォームを再表示
        }

        try {
            // @AuthenticationPrincipalのuserはログイン時点のスナップショット（セッションに保持されたdetachedな
            // JPAエンティティ）で、以降の更新は反映されない。これをそのままsave()するとmergeとして働き、
            // 他の全カラムがログイン時点の値で上書きされてしまう（例: 別セッションでの変更が巻き戻る）。
            // そのため必ずDBから最新のエンティティを取り直し、そちらだけを書き換えて保存する。
            Accounts current = accountsRepository.findById(user.getUserId()).orElseThrow();

            // ユーザー名・メールアドレスは必須項目のため常に反映する
            current.setUsername(form.getUsername());
            current.setEmail(form.getEmail());
            if (form.getPassword() != null && !form.getPassword().isEmpty()) {
                // パスワードはハッシュ化して更新。current には直前にセットしたusername/emailも
                // 反映済みのため、updatePassword内のaccountsRepository.save(current)でまとめて保存される
                accountsService.updatePassword(current, form.getPassword());
            } else {
                accountsRepository.save(current);
            }
            // 成功メッセージをリダイレクト先に渡す（PRGパターン）
            redirectAttributes.addFlashAttribute("flashMessage", InfoMessages.INFO_EDIT_SUCCESS);
            redirectAttributes.addFlashAttribute("flashType", "success");
            return "redirect:/settings";

        } catch (Exception e) {
            log.error("設定の保存に失敗しました", e);
            form.setPassword("");
            model.addAttribute("flashMessage", ErrorMessages.ERROR_UPDATE_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_setting";
        }
    }
    
 // GETリクエスト時（アカウント追加フォームの初期表示）
    @GetMapping("/add/general")
    public String addGeneralForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
        return "pf1/user/accounts_add";
    }

    // POSTリクエスト時（アカウントの登録）
    @PostMapping("/add/general")
    public String addGeneral(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes,
            Model model) {

        // パスワードのバリデーション（「ユーザーマスタ作成」タスクで作成済みの AccountsValidator）
        String passwordError = accountsValidator.validatePassword(form.getPassword());
        if (passwordError != null) {
            bindingResult.rejectValue("password", "error", passwordError);
        }

        // パスワード一致チェック
        String matchError = accountsValidator.validatePasswordMatch(form.getPassword(), form.getPasswordCheck());
        if (matchError != null) {
            bindingResult.rejectValue("passwordCheck", "error", matchError);
        }

        // ふりがなのバリデーション（「バリデーション：ふりがな」タスクで作成済み）
        if (form.getFurigana() != null && !form.getFurigana().isEmpty()) {
            String furiganaError = accountsValidator.validateFurigana(form.getFurigana());
            if (furiganaError != null) {
                bindingResult.rejectValue("furigana", "error", furiganaError);
            }
        }

        // 性別のバリデーション
        if (form.getGender() != null && !form.getGender().isEmpty()) {
            String genderError = accountsValidator.validateGender(form.getGender());
            if (genderError != null) {
                bindingResult.rejectValue("gender", "error", genderError);
            }
        }

        // ユーザー名重複チェック（「バリデーション：ユーザー名」タスクで作成済み）
        if (form.getUsername() != null && accountsRepository.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue("username", "error", ErrorMessages.ERROR_USERNAME_EXISTS);
        }

        // メールアドレス重複チェック
        if (form.getEmail() != null && accountsRepository.existsByEmail(form.getEmail())) {
            bindingResult.rejectValue("email", "error", ErrorMessages.ERROR_EMAIL_EXISTS);
        }

        // 画像サイズチェック（保存はまだしない。「プロフィール画像」タスクで作成済みの saveProfileImage）
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageSizeError = accountsValidator.validateProfileImageSize(profileImage.getSize());
            if (imageSizeError != null) {
                bindingResult.rejectValue("profileImagePath", "error", imageSizeError);
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
            model.addAttribute("flashMessage", ErrorMessages.ERROR_REGISTRATION_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_add";
        }

        try {
            // 他の全項目の検証を通過してから、ここで初めて画像をディスクに書き込む。
            // バリデーション前に保存すると、他項目のエラーで戻ったときにどのレコードにも
            // 紐付かない孤児ファイルがディスクに残り続けてしまうため。
            String profileImagePath = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImagePath = saveProfileImage(profileImage);
            }

            // ユーザー登録（「ユーザーマスタ作成」タスクで作成済みの AccountsService.createUser）
            accountsService.createUser(
                form.getUsername(),
                form.getPassword(),
                form.getEmail(),
                form.getFurigana(),
                form.getGender(),
                form.getAge(),
                form.getProfile(),
                profileImagePath
            );
            redirectAttributes.addFlashAttribute("flashMessage", InfoMessages.INFO_REGISTRATION_SUCCESS);
            redirectAttributes.addFlashAttribute("flashType", "success");
            return "redirect:/add/general";
        } catch (Exception e) {
            model.addAttribute("genderList", AccountsFormConstants.GENDER_LIST);
            model.addAttribute("flashMessage", ErrorMessages.ERROR_REGISTRATION_FAILED);
            model.addAttribute("flashType", "error");
            return "pf1/user/accounts_add";
        }
    }

    @Value("${app.media.location}")
    private String mediaLocation;  // application.properties のメディア保存先

    // 拡張子はサーバー側で許可リストと突き合わせる（クライアントが送ってきた文字列をそのまま
    // ファイルパスに使うと、細工されたファイル名で意図しない場所に書き込まれる恐れがあるため）
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

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
        String filename = UUID.randomUUID() + extension;  // ファイル名の重複を避けるためUUIDを使用

        Path uploadDir = Paths.get(mediaLocation, "profile_images");
        Files.createDirectories(uploadDir);  // フォルダが存在しない場合は作成

        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath);

        return "profile_images/" + filename;  // DB に保存するパス
    }
}