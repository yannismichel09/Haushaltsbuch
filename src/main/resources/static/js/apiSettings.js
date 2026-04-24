async function requestSettings(path, method, payload, expectsJson = true) {
    try {
        const response = await fetch(SETTINGS_BASE_PATH + "/" + currentUserId + path, {
            method: method,
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken,
                "Content-Type": "application/json"
            },
            body: payload ? JSON.stringify(payload) : undefined
        });

        if (!response.ok) {
            throw new Error("API Settings error: " + method + " " + path);
        }

        return expectsJson ? await response.json() : true;
    } catch (error) {
        console.log(error);
    }
}

// Ruft die Einstellungen des aktuell angemeldeten Benutzers ab
async function getUserSettings() {
    return await requestSettings("", "GET");
}

// Aktualisiert die E-Mail des aktuell angemeldeten Benutzers
async function updateEmail(emailDtoIn) {
    return await requestSettings("/email", "PUT", emailDtoIn);
}

// Aktualisiert das Passwort des aktuell angemeldeten Benutzers
async function updatePassword(passwordDtoIn) {
    return await requestSettings("/password", "PUT", passwordDtoIn, false);
}

// Aktualisiert den Benutzernamen des aktuell angemeldeten Benutzers
async function updateUsername(usernameDtoIn) {
    return await requestSettings("/username", "PUT", usernameDtoIn);
}

// Aktualisiert das Profilbild des aktuell angemeldeten Benutzers
async function updateProfilePicture(profilePictureDtoIn) {
    return await requestSettings("/profile-picture", "PUT", profilePictureDtoIn);
}

// Aktualisiert E-Mail und optional Passwort in einem gemeinsamen Save
async function updateSecurity(securityDtoIn) {
    return await requestSettings("/security", "PUT", securityDtoIn);
}