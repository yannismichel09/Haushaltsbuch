// Ruft die Kategorien ab, bei denen das Budget über einen bestimmten Prozentsatz liegt (90%) / Wird für die Warnung benötigt
async function checkBudgetLimit() {
    try {
        let response = await fetch(WARNING_BASE_PATH+"/budgetlimit", {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken
            }
        });

        if (!response.ok) {
            throw new Error("API Warning error: checkBudgetLimit");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Ruft ab, ob die Ausgaben höher sind als die Einnahmen (Saldo) / Wird für die Warnung benötigt
async function checkNetBalanceNegative() {
    try {
        let response = await fetch(WARNING_BASE_PATH+"/netbalance", {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Authorization": globalToken
            }
        });

        if (!response.ok) {
            throw new Error("API Warning error: checkNetBalanceNegative");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}