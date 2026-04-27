document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('globalToken');
    const user = localStorage.getItem('currentUser');

    if (!token || !user) {
        window.location.href = "welcome.html";
    }
});