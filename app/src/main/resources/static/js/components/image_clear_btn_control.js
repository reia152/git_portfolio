document.addEventListener("DOMContentLoaded", function () {
  const checkbox = document.getElementById("clear-profile-image");
  const label = document.getElementById("clear-image-label");

  function updateDeleteButton() {
    if (checkbox.checked) {
      label.classList.remove("btn-outline-danger");
      label.classList.add("btn-danger");
      label.textContent = "画像を削除:ON";
    } else {
      label.classList.remove("btn-danger");
      label.classList.add("btn-outline-danger");
      label.textContent = "画像を削除:OFF";
    }
  }

  if (checkbox && label) {
    checkbox.addEventListener("change", updateDeleteButton);
    updateDeleteButton();
  }
});