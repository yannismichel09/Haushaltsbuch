let globalToken = localStorage.getItem("globalToken") || null;
let currentUser = JSON.parse(localStorage.getItem("currentUser")) || null;
let currentUserId = currentUser ? currentUser.userId : null;

const CATEGORY_BASE_PATH  = "/categories";
const CSV_BASE_PATH = "/csv";
const DASHBOARD_BASE_PATH = "/dashboard";
const SETTINGS_BASE_PATH = "/settings";
const TRANSACTION_BASE_PATH = "/transactions";
const USER_BASE_PATH = "/users";
const WARNING_BASE_PATH = "/warnings";

function saveSession(token, user) {
    localStorage.setItem("globalToken", token);
    localStorage.setItem("currentUser", JSON.stringify(user));
    
    globalToken = token;
    currentUser = user;
    currentUserId = user.userId;
}

function logout() {
    localStorage.clear();
    window.location.href = "welcome.html";
}

function handleApiErrorResponse(response, context) {
    if (response.ok) {
        return;
    }

    if (response.status === 401 || response.status === 403) {
        alert("Your session has expired. Please log in again.");
        logout();
        throw new Error("API Auth error: " + context + " (" + response.status + ")");
    }

    throw new Error("API Error: " + context + " (" + response.status + ")");
}