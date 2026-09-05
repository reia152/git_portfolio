const sidebar = document.getElementById("sidebar");
const overlay = document.getElementById("overlay");
const hoverArea = document.getElementById("sidebar-hover");

hoverArea.addEventListener("mouseenter", () => {
  sidebar.style.left = "0";
  overlay.style.display = "block";
});

let lastMouseX = 0;
let lastMouseY = 0;
document.addEventListener("mousemove", (e) => {
  lastMouseX = e.clientX;
  lastMouseY = e.clientY;
});

function tryHideSidebar() {
  setTimeout(() => {
    const isHoveringSidebar = sidebar.matches(":hover");
    const isHoveringHoverArea = hoverArea.matches(":hover");

    const sidebarRect = sidebar.getBoundingClientRect();
    const isWithinSafeX = lastMouseX < sidebarRect.left + 150;
    const isAboveHeader = lastMouseY < 60;

    const shouldHide =
      !isHoveringSidebar &&
      !isHoveringHoverArea &&
      (!isWithinSafeX || isAboveHeader);

    if (shouldHide) {
      sidebar.style.left = "-350px";
      overlay.style.display = "none";
    }
  }, 100);
}

sidebar.addEventListener("mouseleave", tryHideSidebar);
hoverArea.addEventListener("mouseleave", tryHideSidebar);