// Erstellt eine neue Kategorie.
async function createCategory(categoryCreateDtoIn) {
	try {
		if (!globalToken) {
			throw new Error("Not authenticated. Please log in again.");
		}

		let response = await fetch(CATEGORY_BASE_PATH, {
			method: "POST",
			headers: {
				"Accept": "application/json",
				"Content-Type": "application/json",
				"Authorization": globalToken
			},
			body: JSON.stringify(categoryCreateDtoIn)
		});

		if (!response.ok) {
			if (response.status === 400 || response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired. Please log in again.");
			}

			let errorText = await response.text();
			throw new Error("API Category error: createCategory (" + response.status + ") " + errorText);
		}

		return await response.json();
	} catch (error) {
		console.error(error);
		throw error;
	}
}

// Gibt alle Kategorien zurück.
async function getAllCategories() {
	try {
		let response = await fetch(CATEGORY_BASE_PATH, {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Category error: getAllCategories");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Gibt eine Kategorie anhand der ID zurück.
async function getCategoryById(categoryId) {
	try {
		let response = await fetch(CATEGORY_BASE_PATH + "/" + categoryId, {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Category error: getCategoryById");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Gibt gefilterte Kategorien zurück.
async function getFilteredCategories(categoryFilterDtoIn) {
	try {
		let response = await fetch(CATEGORY_BASE_PATH + "/filter", {
			method: "POST",
			headers: {
				"Accept": "application/json",
				"Content-Type": "application/json",
				"Authorization": globalToken
			},
			body: JSON.stringify(categoryFilterDtoIn)
		});

		if (!response.ok) {
			throw new Error("API Category error: getFilteredCategories");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Löscht eine Kategorie anhand der ID.
async function deleteCategory(categoryId) {
	try {
		let response = await fetch(CATEGORY_BASE_PATH + "/" + categoryId, {
			method: "DELETE",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Category error: deleteCategory");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Aktualisiert eine Kategorie anhand der ID.
async function updateCategory(categoryId, categoryUpdateDtoIn) {
	try {
		let response = await fetch(CATEGORY_BASE_PATH + "/" + categoryId, {
			method: "PUT",
			headers: {
				"Accept": "application/json",
				"Content-Type": "application/json",
				"Authorization": globalToken
			},
			body: JSON.stringify(categoryUpdateDtoIn)
		});

		if (!response.ok) {
			throw new Error("API Category error: updateCategory");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}
