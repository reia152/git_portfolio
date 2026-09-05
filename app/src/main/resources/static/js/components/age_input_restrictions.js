document.addEventListener("DOMContentLoaded", () => {
  const numberInput = document.getElementById("id_age");

  if (numberInput) {
    numberInput.addEventListener("input", () => {
      numberInput.value = numberInput.value.replace(/\D/g, "");
      if (numberInput.value.length > 3) {
        numberInput.value = numberInput.value.slice(0, 3);
      }
    });
  }
});