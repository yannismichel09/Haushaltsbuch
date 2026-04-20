function updateHeaderUI() {
  const usernameElement = document.getElementById('username');
  const profileImgElement = document.getElementById('profilePicture');

  if (currentUser) {
      if (usernameElement) {
          usernameElement.textContent = currentUser.username;
      }

      if (profileImgElement) {
          if (currentUser.userProfilePicture) {
              profileImgElement.src = "data:image/png;base64," + currentUser.userProfilePicture;
          } else {
              profileImgElement.src = "icons/Gray-Profile-Picture.jpeg";
          }
      }
  }
}

// Setzt die Höhe des iframes passend zum aktuellen Inhaltsdokument.
function resizeFrame(iframe) {
if (!iframe) {
  return;
}

try {
  const frameDocument = iframe.contentWindow.document;
  const newHeight = Math.max(
    frameDocument.body.scrollHeight,
    frameDocument.documentElement.scrollHeight
  );
  iframe.style.height = `${newHeight}px`;
} catch (error) {

}
}

// Initialisiert automatische Höhen-Updates bei Inhaltsänderungen im iframe.
function initAutoResizeForFrame(iframe) {
if (!iframe) {
  return;
}

iframe.addEventListener('load', () => {
  resizeFrame(iframe);

  try {
    const frameDocument = iframe.contentWindow.document;
    const observer = new ResizeObserver(() => resizeFrame(iframe));
    observer.observe(frameDocument.body);
  } catch (error) {

  }
});
}

// Aktiviert das automatische iframe-Resizing und Header-Update nach dem Laden der Seite.
document.addEventListener('DOMContentLoaded', () => {

// 1. Header Daten aktualisieren
updateHeaderUI();

// 2. Iframe Logik
const frames = document.querySelectorAll('.tab-frame');
const tabButtons = document.querySelectorAll('.tablinks');

frames.forEach((frame) => {
  initAutoResizeForFrame(frame);
});

tabButtons.forEach((button) => {
  button.addEventListener('click', () => {
    setTimeout(() => {
      frames.forEach((frame) => resizeFrame(frame));
    }, 0);
  });
});

window.addEventListener('resize', () => {
  frames.forEach((frame) => resizeFrame(frame));
});
});

function toggleDropdown() {
  document.getElementById("dropdownMenu").classList.toggle("show");
}

window.onclick = function(event) {
  if (!event.target.matches('#profilePicture')) {
      const dropdowns = document.getElementsByClassName("dropdown-content");
      for (let i = 0; i < dropdowns.length; i++) {
          let openDropdown = dropdowns[i];
          if (openDropdown.classList.contains('show')) {
              openDropdown.classList.remove('show');
          }
      }
  }
}