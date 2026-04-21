// Prueft, ob fuer den Request ein gueltiger Login-Token vorhanden ist.
function ensureCategoryAuth() {
	if (!globalToken) {
		throw new Error("Not authenticated. Please log in again.");
	}
}

// Wertet fehlgeschlagene API-Antworten einheitlich aus und liefert eine sprechende Fehlermeldung.
async function throwCategoryApiError(response, actionName) {
	if (response.status === 400 || response.status === 401 || response.status === 403) {
		logout();
		throw new Error("Session expired. Please log in again.");
	}

	let errorText = await response.text();
	let parsedError = null;

	try {
		parsedError = errorText ? JSON.parse(errorText) : null;
	} catch (_) {
		parsedError = null;
	}

	const rawMessage = (parsedError && parsedError.message) ? parsedError.message : errorText;
	const normalizedMessage = (rawMessage || "").toLowerCase();
	let userMessage = "Category request failed (" + response.status + ").";

	if ((actionName === "createCategory" || actionName === "updateCategory") &&
		(response.status === 409 || normalizedMessage.includes("unique") || normalizedMessage.includes("duplicate"))) {
		userMessage = "A category with this name already exists.";
	} else if (actionName === "createCategory" && response.status === 500) {
		userMessage = "Category could not be created. If this name already exists, please choose a different one.";
	}

	const error = new Error(userMessage);
	error.status = response.status;
	error.actionName = actionName;
	error.rawErrorText = errorText;
	error.rawErrorMessage = rawMessage;
	throw error;
}

// Fuehrt einen Kategorien-Request aus und gibt die JSON-Antwort zurueck.
async function sendCategoryRequest(path, method, actionName, body = null) {
	ensureCategoryAuth();

	const headers = {
		"Accept": "application/json",
		"Authorization": globalToken
	};

	if (body !== null) {
		headers["Content-Type"] = "application/json";
	}

	const response = await fetch(CATEGORY_BASE_PATH + path, {
		method,
		headers,
		body: body !== null ? JSON.stringify(body) : null
	});

	if (!response.ok) {
		await throwCategoryApiError(response, actionName);
	}

	return await response.json();
}

// Erstellt eine neue Kategorie.
async function createCategory(categoryCreateDtoIn) {
	try {
		return await sendCategoryRequest("", "POST", "createCategory", categoryCreateDtoIn);
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Gibt alle Kategorien zurück.
async function getAllCategories() {
	try {
		return await sendCategoryRequest("", "GET", "getAllCategories");
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Gibt eine Kategorie anhand der ID zurück.
async function getCategoryById(categoryId) {
	try {
		return await sendCategoryRequest("/" + categoryId, "GET", "getCategoryById");
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Gibt gefilterte Kategorien zurück.
async function getFilteredCategories(categoryFilterDtoIn) {
	try {
		return await sendCategoryRequest("/filter", "POST", "getFilteredCategories", categoryFilterDtoIn);
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Löscht eine Kategorie anhand der ID.
async function deleteCategory(categoryId) {
	try {
		return await sendCategoryRequest("/" + categoryId, "DELETE", "deleteCategory");
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Aktualisiert eine Kategorie anhand der ID.
async function updateCategory(categoryId, categoryUpdateDtoIn) {
	try {
		return await sendCategoryRequest("/" + categoryId, "PUT", "updateCategory", categoryUpdateDtoIn);
	} catch (error) {
		console.error(error);
		throw error;
	}
}
