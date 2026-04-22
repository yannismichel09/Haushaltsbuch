// Liest den Dateinamen aus dem Response-Header aus.
function parseCsvFilename(contentDisposition, fallbackName) {
	if (!contentDisposition) {
		return fallbackName;
	}

	const match = contentDisposition.match(/filename\*?=(?:UTF-8''|\")?([^\";]+)/i);
	if (!match || !match[1]) {
		return fallbackName;
	}

	try {
		return decodeURIComponent(match[1]).replace(/"/g, "").trim();
	} catch (error) {
		return match[1].replace(/"/g, "").trim();
	}
}

// Startet den Download einer CSV-Datei im Browser.
function triggerCsvDownload(csvContent, filename) {
	const normalizedCsvContent = String(csvContent ?? "").replace(/\r?\n/g, "\r\n");
	const csvBlob = new Blob(["\uFEFF", normalizedCsvContent], { type: "text/csv;charset=utf-8" });
	const downloadUrl = URL.createObjectURL(csvBlob);

	const link = document.createElement("a");
	link.href = downloadUrl;
	link.download = filename;
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);

	window.setTimeout(() => URL.revokeObjectURL(downloadUrl), 1000);
}

// Exportiert gefilterte Kategorien als CSV.
async function exportFilteredCategoriesToCsv(categoryFilterDtoIn = null) {
	try {
		const requestOptions = {
			method: "POST",
			headers: {
				"Accept": "text/csv",
				"Authorization": globalToken
			}
		};

		if (categoryFilterDtoIn !== null) {
			requestOptions.headers["Content-Type"] = "application/json";
			requestOptions.body = JSON.stringify(categoryFilterDtoIn);
		}

		const response = await fetch(CSV_BASE_PATH + "/categories", requestOptions);

		if (!response.ok) {
			throw new Error("API CSV error: exportFilteredCategoriesToCsv");
		}

		const csvContent = await response.text();
		const contentDisposition = response.headers.get("content-disposition");
		const filename = parseCsvFilename(contentDisposition, "categories.csv");

		triggerCsvDownload(csvContent, filename);
		return csvContent;
	} catch (error) {
		console.log(error);
	}
}

// Exportiert gefilterte Transaktionen als CSV.
async function exportFilteredTransactionsToCsv(transactionFilterDtoIn = null) {
	try {
		const requestOptions = {
			method: "POST",
			headers: {
				"Accept": "text/csv",
				"Authorization": globalToken
			}
		};

		if (transactionFilterDtoIn !== null) {
			requestOptions.headers["Content-Type"] = "application/json";
			requestOptions.body = JSON.stringify(transactionFilterDtoIn);
		}

		const response = await fetch(CSV_BASE_PATH + "/transactions", requestOptions);

		if (!response.ok) {
			throw new Error("API CSV error: exportFilteredTransactionsToCsv");
		}

		const csvContent = await response.text();
		const contentDisposition = response.headers.get("content-disposition");
		const filename = parseCsvFilename(contentDisposition, "transactions.csv");

		triggerCsvDownload(csvContent, filename);
		return csvContent;
	} catch (error) {
		console.log(error);
	}
}
