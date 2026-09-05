const loginForm = document.getElementById("login-form");
if (loginForm) {
  loginForm.addEventListener("submit", (event) => {
    let hasEmpty = false;
    const requiredInputs = loginForm.querySelectorAll("input[required]");
    requiredInputs.forEach((input) => {
      if (!input.value.trim()) {
        input.classList.add("bg-danger");
        hasEmpty = true;
      } else {
        input.classList.remove("bg-danger");
      }
    });
    if (hasEmpty) {
      event.preventDefault();
    }
  });
}