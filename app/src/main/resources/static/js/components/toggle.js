document.addEventListener("DOMContentLoaded", () => {
  const toggles = document.querySelectorAll(".toggle-password");

  toggles.forEach((toggle) => {
    const targetSelector = toggle.dataset.target;
    const input = document.querySelector(targetSelector);

    const showIcon = toggle.querySelector(".show-icon");
    const hideIcon = toggle.querySelector(".hide-icon");

    if (!input || !showIcon || !hideIcon) return;

    toggle.addEventListener("click", () => {
      const isPassword = input.type === "password";
      input.type = isPassword ? "text" : "password";
      showIcon.classList.toggle("d-none", isPassword);
      hideIcon.classList.toggle("d-none", !isPassword);
    });

    if (
      input.classList.contains("is-invalid") ||
      input.classList.contains("is-valid")
    ) {
      toggle.classList.add("validated-id-password");
    }
  });
});