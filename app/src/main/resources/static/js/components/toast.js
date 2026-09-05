document.addEventListener("DOMContentLoaded", () => {
  const DataEl = document.querySelector(".toast-body");
  const Message = DataEl?.textContent;

  if (Message && Message.trim() !== "") {
    const toastEl = document.getElementById("toast-contents");
    const toast = new bootstrap.Toast(toastEl);
    toast.show();
  }
});