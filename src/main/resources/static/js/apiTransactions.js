// Erstellte eine neue Transaktion
async function createTransaction(transactionDtoIn) {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': globalToken
            },
            body: JSON.stringify(transactionDtoIn)
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: createTransaction");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Liefert alle Transaktionen zurück
async function getTransactions() {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Authorization': globalToken
            }
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: getTransactions");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Liefert eine Transaktion zurück
async function getTransaction(transactionId) {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH + "/" + transactionId, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Authorization': globalToken
            }
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: getTransaction");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Liefert alle gefilterten Transaktionen zurück
async function getFilteredTransactions(transactionFilterDtoIn) {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH + "/filter", {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                "Content-Type": "application/json",
                'Authorization': globalToken
            },
            body: JSON.stringify(transactionFilterDtoIn)
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: getFilteredTransactions");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Löscht eine Transaktion
async function deleteTransaction(transactionId) {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH + "/" + transactionId, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Authorization': globalToken
            }
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: deleteTransaction");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}

// Aktualisiert eine Transaktion
async function updateTransaction(transactionId, transactionUpdateDtoIn) {
    try {
        let response = await fetch(TRANSACTION_BASE_PATH + "/" + transactionId, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': globalToken
            },
            body: JSON.stringify(transactionUpdateDtoIn)
        });

        if (!response.ok) {
            throw new Error("API Transaction Error: updateTransaction");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
    }
}