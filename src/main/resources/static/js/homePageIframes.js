// Setzt die Hoehe des iframes passend zum aktuellen Inhaltsdokument.
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

// Initialisiert automatische Hoehen-Updates bei Inhaltsaenderungen im iframe.
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

// Aktiviert das automatische iframe-Resizing nach dem Laden der Seite.
document.addEventListener('DOMContentLoaded', () => {
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
