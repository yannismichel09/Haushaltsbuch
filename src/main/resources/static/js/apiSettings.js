// Ruft die Einstellungen des aktuell angemeldeten Benutzers ab
async function getUserSettings() {
    try {
         let response = await fetch(SETTINGS_BASE_PATH + "/"+ currentUserId, { 
             method: "GET",
             headers: {
                "Accept": "application/json",
                "Authorization": globalToken
            }
          });
          
          if(!response.ok) {
            throw new Error("API Settings error: getUserSettings");
          }
          
          return await response.json();
    } catch (error) {
            console.log(error);
    }
}

// Aktualisiert die E-Mail des aktuell angemeldeten Benutzers
async function updateEmail(emailDtoIn) {
    try {
        let response = await fetch(SETTINGS_BASE_PATH + "/" + currentUserId + "/email", {
            method: "PUT",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(emailDtoIn)
        });

        if(!response.ok) {
            throw new Error("API Settings error: updateEmail");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Aktualisiert das Passwort des aktuell angemeldeten Benutzers
async function updatePassword(passwordDtoIn) {
    try {
        let response = await fetch(SETTINGS_BASE_PATH + "/" + currentUserId + "/password", {
            method: "PUT",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(passwordDtoIn)
        });

        if(!response.ok) {
            throw new Error("API Settings error: updatePassword");
        }

        return response.ok;
    } catch (error) {
        console.log(error);
    }
}

// Aktualisiert den Benutzernamen des aktuell angemeldeten Benutzers
async function updateUsername(usernameDtoIn) {
    try {
        let response = await fetch(SETTINGS_BASE_PATH + "/" + currentUserId + "/username", {
            method: "PUT",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(usernameDtoIn)
        });

        if(!response.ok) {
            throw new Error("API Settings error: updateUsername");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Aktualisiert das Profilbild des aktuell angemeldeten Benutzers
async function updateProfilePicture(profilePictureDtoIn) {
    try {
        let response = await fetch(SETTINGS_BASE_PATH + "/" + currentUserId + "/profile-picture", {
            method: "PUT",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(profilePictureDtoIn)
        });

        if(!response.ok) {
            throw new Error("API Settings error: updateProfilePicture");
        }
    } catch (error) {
        console.log(error);
    }
}