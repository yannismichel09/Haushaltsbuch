// Setzt die aktuelle Benutzersitzung im Frontend zurück.
function clearUserSession() {
	globalToken = null;
	currentUser = null;
	currentUserId = null;
}

// Registriert einen neuen Benutzer über die User-API.
async function registerUser(userRegisterDtoIn) {
	try {
		let response = await fetch(USER_BASE_PATH + "/register", {
			method: "POST",
			headers: {
				"Accept": "application/json",
				"Content-Type": "application/json"
			},
			body: JSON.stringify(userRegisterDtoIn)
		});

		if (!response.ok) {
			throw new Error("API User error: registerUser");
		}

		const authPayload = await response.json();
		globalToken = authPayload.token;
		currentUser = authPayload.user;
		currentUserId = authPayload.user.userId;

		return authPayload;
	} catch (error) {
		console.log(error);
	}
}

// Meldet einen Benutzer an und speichert Token sowie Benutzerdaten.
async function loginUser(userLoginDtoIn) {
	try {
		let response = await fetch(USER_BASE_PATH + "/login", {
			method: "POST",
			headers: {
				"Accept": "application/json",
				"Content-Type": "application/json"
			},
			body: JSON.stringify(userLoginDtoIn)
		});

		if (!response.ok) {
			throw new Error("API User error: loginUser");
		}

		const authPayload = await response.json();
		globalToken = authPayload.token;
		currentUser = authPayload.user;
		currentUserId = authPayload.user.userId;

		return authPayload;
	} catch (error) {
		console.log(error);
	}
}

// Meldet den aktuell angemeldeten Benutzer ab.
async function logoutUser() {
	try {
		let response = await fetch(USER_BASE_PATH + "/logout", {
			method: "POST",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API User error: logoutUser");
		}

		const wasLoggedOut = await response.json();
		if (wasLoggedOut) {
			clearUserSession();
		}

		return wasLoggedOut;
	} catch (error) {
		console.log(error);
	}
}

// Loescht den Benutzer anhand der User-ID.
async function deleteUser(userId) {
	try {
		let response = await fetch(USER_BASE_PATH + "/" + userId, {
			method: "DELETE",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API User error: deleteUser");
		}

		const wasDeleted = await response.json();
		if (wasDeleted) {
			clearUserSession();
		}

		return wasDeleted;
	} catch (error) {
		console.log(error);
	}
}

