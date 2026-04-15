// Lädt alle Transaktionen für die Dashboard-Ansicht.
async function getAllDashboardTransactions() {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/transactions", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getAllDashboardTransactions");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Lädt gefilterte Transaktionen für das Dashboard.
async function getFilteredDashboardTransactions(transactionFilterDtoIn) {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/transactions/filter", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken,
				"Content-Type": "application/json"
			},
			body: JSON.stringify(transactionFilterDtoIn)
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getFilteredDashboardTransactions");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Lädt die Summe aller Ausgaben.
async function getDashboardSumSpendings() {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/transactions/sumspendings", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getDashboardSumSpendings");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Lädt die Summe aller Einnahmen.
async function getDashboardSumIncome() {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/transactions/sumincome", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getDashboardSumIncome");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Lädt die Ausgabensumme einer Kategorie.
async function getDashboardCategorySumSpendings(categoryId) {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/category/" + categoryId + "/sumspendings", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getDashboardCategorySumSpendings");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}

// Lädt die Einnahmensumme einer Kategorie.
async function getDashboardCategorySumIncome(categoryId) {
	try {
		let response = await fetch(DASHBOARD_BASE_PATH + "/category/" + categoryId + "/sumincome", {
			method: "GET",
			headers: {
				"Accept": "application/json",
				"Authorization": globalToken
			}
		});

		if (!response.ok) {
			throw new Error("API Dashboard error: getDashboardCategorySumIncome");
		}

		return await response.json();
	} catch (error) {
		console.log(error);
	}
}