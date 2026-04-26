const DEFAULT_PROFILE_PICTURE_PATH = "icons/Gray-Profile-Picture.jpeg";

let originalUsername = "";
let originalEmail = "";
let originalProfilePictureSrc = DEFAULT_PROFILE_PICTURE_PATH;
let pendingProfilePictureBase64 = null;

function elementById(id) {
    return document.getElementById(id);
}

function buildProfilePictureSrc(profilePicture) {
    if (!profilePicture) {
        return DEFAULT_PROFILE_PICTURE_PATH;
    }

    if (typeof profilePicture === "string") {
        return "data:image/png;base64," + profilePicture;
    }

    if (Array.isArray(profilePicture)) {
        const bytes = new Uint8Array(profilePicture);
        let binary = "";
        for (let i = 0; i < bytes.length; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return "data:image/png;base64," + btoa(binary);
    }

    return DEFAULT_PROFILE_PICTURE_PATH;
}

function clearPasswordInputs() {
    elementById("oldPasswordInput").value = "";
    elementById("passwordInput").value = "";
    elementById("passwordConfirmInput").value = "";
}

function resetSecurityInputs() {
    elementById("emailInput").value = originalEmail;
    clearPasswordInputs();
}

function applyAndSyncUser(user) {
    applySettingsToForm(user);
    syncSessionUser(user);
}

function logoutFromCurrentContext() {
    if (window.parent && window.parent !== window && typeof window.parent.logout === "function") {
        window.parent.logout();
        return;
    }

    logout();
}

function syncSessionUser(updatedUser) {
    if (!updatedUser) {
        return;
    }

    saveSession(globalToken, updatedUser);

    if (window.parent && window.parent !== window) {
        if (typeof window.parent.saveSession === "function") {
            window.parent.saveSession(window.parent.globalToken || globalToken, updatedUser);
        }

        if (typeof window.parent.updateHeaderUI === "function") {
            window.parent.updateHeaderUI();
        }
    }
}

function applySettingsToForm(user) {
    if (!user) {
        return;
    }

    originalUsername = user.username || "";
    originalEmail = user.email || "";

    const profilePictureElement = elementById("profilePicture");
    const usernameInput = elementById("usernameInput");
    const emailInput = elementById("emailInput");

    originalProfilePictureSrc = buildProfilePictureSrc(user.profilePicture);
    pendingProfilePictureBase64 = null;

    profilePictureElement.src = originalProfilePictureSrc;
    usernameInput.value = originalUsername;
    emailInput.value = originalEmail;
}

async function loadSettings() {
    const user = await getUserSettings();
    if (!user) {
        alert("Settings could not be loaded.");
        return;
    }

    applyAndSyncUser(user);
}

async function saveAccountHandler() {
    const usernameInput = elementById("usernameInput");
    const profilePictureElement = elementById("profilePicture");
    const username = usernameInput.value.trim();

    if (!username) {
        alert("Username must not be empty.");
        usernameInput.value = originalUsername;
        return;
    }

    const usernameChanged = username !== originalUsername;
    const profilePictureChanged = pendingProfilePictureBase64 !== null;

    if (!usernameChanged && !profilePictureChanged) {
        alert("No changes to save.");
        return;
    }

    let latestUser = null;

    if (usernameChanged) {
        latestUser = await updateUsername({ username });
        if (!latestUser) {
            alert("Username could not be saved.");
            usernameInput.value = originalUsername;
            return;
        }
    }

    if (profilePictureChanged) {
        const updatedWithPicture = await updateProfilePicture({ profilePicture: pendingProfilePictureBase64 });
        if (!updatedWithPicture) {
            alert("Profile picture could not be saved.");

            if (latestUser) {
                applyAndSyncUser(latestUser);
            } else {
                profilePictureElement.src = originalProfilePictureSrc;
            }
            return;
        }

        latestUser = updatedWithPicture;
    }

    applyAndSyncUser(latestUser);
    alert("Account details were saved.");
}

function triggerProfilePictureSelection() {
    const input = elementById("profile-picture-input");
    input.value = "";
    input.click();
}

function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => {
            const dataUrl = reader.result;
            if (typeof dataUrl !== "string") {
                reject(new Error("Invalid file content"));
                return;
            }

            const commaIndex = dataUrl.indexOf(",");
            if (commaIndex < 0) {
                reject(new Error("Invalid data URL"));
                return;
            }

            resolve(dataUrl.substring(commaIndex + 1));
        };
        reader.onerror = () => reject(reader.error || new Error("Datei konnte nicht gelesen werden."));
        reader.readAsDataURL(file);
    });
}

async function queueProfilePictureHandler(event) {
    const file = event.target.files && event.target.files[0];
    if (!file) {
        return;
    }

    try {
        const base64Picture = await fileToBase64(file);
        pendingProfilePictureBase64 = base64Picture;

        const profilePictureElement = elementById("profilePicture");
        profilePictureElement.src = "data:image/png;base64," + base64Picture;
        alert("Profile picture selected. It will be saved only when you click Save.");
    } catch (error) {
        console.error(error);
        alert("Profile picture could not be processed.");
    }
}

async function saveSecurityHandler() {
    const emailInput = elementById("emailInput");
    const oldPasswordInput = elementById("oldPasswordInput");
    const passwordInput = elementById("passwordInput");
    const passwordConfirmInput = elementById("passwordConfirmInput");

    const email = emailInput.value.trim();
    const oldPassword = oldPasswordInput.value;
    const newPassword = passwordInput.value;
    const confirmPassword = passwordConfirmInput.value;

    const emailChanged = email !== originalEmail;
    const passwordChangeRequested = oldPassword !== "" || newPassword !== "" || confirmPassword !== "";

    if (email === "") {
        alert("Email must not be empty.");
        emailInput.value = originalEmail;
        return;
    }

    if (!emailChanged && !passwordChangeRequested) {
        alert("No changes to save.");
        return;
    }

    if (passwordChangeRequested) {
        if (!oldPassword || !newPassword || !confirmPassword) {
            alert("To change the password, all password fields must be filled in.");
            return;
        }

        if (newPassword !== confirmPassword) {
            alert("New password and confirmation do not match.");
            return;
        }
    }

    const updatedUser = await updateSecurity({
        email,
        oldPassword,
        newPassword,
        confirmPassword
    });

    if (!updatedUser) {
        alert("Security details could not be saved. Please verify your password.");
        return;
    }

    applyAndSyncUser(updatedUser);
    clearPasswordInputs();
    alert("Security details were saved.");
}

async function deleteAccountHandler() {
    const confirmation = window.confirm("Do you really want to delete your account? This action cannot be undone.");
    if (!confirmation) {
        return;
    }

    const wasDeleted = await deleteUser(currentUserId);
    if (!wasDeleted) {
        alert("Account could not be deleted.");
        return;
    }

    logoutFromCurrentContext();
}

document.addEventListener("DOMContentLoaded", () => {
    if (!globalToken || !currentUserId) {
        return;
    }

    elementById("cancel-username-btn").addEventListener("click", () => {
        elementById("usernameInput").value = originalUsername;
        elementById("profilePicture").src = originalProfilePictureSrc;
        pendingProfilePictureBase64 = null;
    });

    elementById("save-username-btn").addEventListener("click", saveAccountHandler);
    elementById("change-profile-picture-btn").addEventListener("click", triggerProfilePictureSelection);
    elementById("profile-picture-input").addEventListener("change", queueProfilePictureHandler);

    elementById("reset-password-btn").addEventListener("click", resetSecurityInputs);
    elementById("save-security-btn").addEventListener("click", saveSecurityHandler);
    elementById("delete-account-btn").addEventListener("click", deleteAccountHandler);

    loadSettings();
});
