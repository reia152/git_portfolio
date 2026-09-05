document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("id_profile_image");
  const previewImg = document.getElementById("preview");

  if (fileInput && previewImg) {
    fileInput.addEventListener("change", function (event) {
      const file = event.target.files[0];

      if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
          previewImg.src = e.target.result;
          previewImg.style.display = "block";
        };
        reader.readAsDataURL(file);
      } else {
        previewImg.style.display = "none";
      }
    });
  }
});