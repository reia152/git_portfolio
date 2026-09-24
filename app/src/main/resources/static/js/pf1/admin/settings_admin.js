document.addEventListener("DOMContentLoaded", () => {
  const invalidInputs = document.querySelectorAll(
    "input.form-control.is-invalid"
  );
  invalidInputs.forEach((input) => {
    input.classList.add("field-error-bg");
  });
});