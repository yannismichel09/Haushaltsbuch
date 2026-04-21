const CATEGORY_COLORS = [
	{ id: "red", hex: "#E53935", label: "Red" },
	{ id: "orange", hex: "#FB8C00", label: "Orange" },
	{ id: "yellow", hex: "#FDD835", label: "Yellow" },
	{ id: "green", hex: "#43A047", label: "Green" },
	{ id: "teal", hex: "#00ACC1", label: "Teal" },
	{ id: "blue", hex: "#1E88E5", label: "Blue" },
	{ id: "indigo", hex: "#3949AB", label: "Indigo" },
	{ id: "purple", hex: "#8E24AA", label: "Purple" },
	{ id: "pink", hex: "#E91E63", label: "Pink" },
	{ id: "brown", hex: "#795548", label: "Brown" },
];

// Erzeugt ein farbiges HTML-Element fuer Picker und Filteranzeige.
function createColorElement(tag, className, hex, title) {
	const el = document.createElement(tag);
	el.className = className;
	el.style.backgroundColor = hex;
	el.title = title;
	return el;
}

// Formatiert ein Budget-Limit als Euro-Betrag fuer die Ausgabe in der Liste.
function formatAmount(amount) {
	if (amount === null || amount === undefined || Number.isNaN(amount)) {
		return "-";
	}

	return new Intl.NumberFormat("de-DE", {
		style: "currency",
		currency: "EUR"
	}).format(amount);
}

// Liest die aktuellen Werte aus dem Kategorie-Formular und baut daraus das DTO fuer die API.
function getCategoryFormValues() {
	const limitRaw = document.getElementById("category-limit").value;

	return {
		categoryName: document.getElementById("category-name").value.trim(),
		categoryDescription: document.getElementById("category-description").value.trim(),
		categoryColor: document.getElementById("category-color").value,
		categoryLimit: limitRaw !== "" ? parseFloat(limitRaw) : null,
	};
}

// Rendert die uebergebenen Kategorien als Karten in den Ergebnisbereich.
function renderCategories(categories) {
	const container = document.getElementById("filtered-categories-list");
	if (!container) {
		return;
	}

	container.innerHTML = "";

	if (!categories || categories.length === 0) {
		const emptyState = document.createElement("p");
		emptyState.className = "categories-empty-state";
		emptyState.textContent = "No categories found.";
		container.appendChild(emptyState);
		return;
	}

	const fragment = document.createDocumentFragment();
	categories.forEach((category) => {
		const card = document.createElement("article");
		card.className = "category-card";

		const titleRow = document.createElement("div");
		titleRow.className = "category-card-title-row";

		const colorDot = document.createElement("span");
		colorDot.className = "category-card-color-dot";
		colorDot.style.backgroundColor = category.categoryColor || "transparent";
		titleRow.appendChild(colorDot);

		const title = document.createElement("h3");
		title.textContent = category.categoryName || "Unnamed category";
		titleRow.appendChild(title);

		const description = document.createElement("p");
		description.className = "category-card-description";
		description.textContent = category.categoryDescription || "No description";

		const limit = document.createElement("p");
		limit.className = "category-card-limit";
		limit.textContent = "Limit: " + formatAmount(category.categoryLimit);

		const actions = document.createElement("div");
		actions.className = "category-card-actions";

		const editButton = document.createElement("button");
		editButton.type = "button";
		editButton.className = "category-card-button category-card-button-edit";
		editButton.textContent = "Edit";
		editButton.dataset.action = "edit";
		editButton.dataset.categoryId = category.categoryId;

		const deleteButton = document.createElement("button");
		deleteButton.type = "button";
		deleteButton.className = "category-card-button category-card-button-delete";
		deleteButton.textContent = "Delete";
		deleteButton.dataset.action = "delete";
		deleteButton.dataset.categoryId = category.categoryId;

		actions.appendChild(editButton);
		actions.appendChild(deleteButton);

		card.appendChild(titleRow);
		card.appendChild(description);
		card.appendChild(limit);
		card.appendChild(actions);
		fragment.appendChild(card);
	});

	container.appendChild(fragment);
}

// Wendet die aktuell ausgewaehlten Such- und Filterwerte auf die geladene Kategorienliste an.
function applyCategoryFilters(categories) {
	const keyword = document.getElementById("search-input")?.value.trim().toLowerCase() || "";
	const amountMinRaw = document.getElementById("filter-amount-min")?.value || "";
	const amountMaxRaw = document.getElementById("filter-amount-max")?.value || "";
	const selectedColors = Array.from(document.querySelectorAll("input[name='categoryColors']:checked"))
		.map((input) => input.value);

	const amountMin = amountMinRaw === "" ? null : parseFloat(amountMinRaw);
	const amountMax = amountMaxRaw === "" ? null : parseFloat(amountMaxRaw);

	return categories.filter((category) => {
		const name = (category.categoryName || "").toLowerCase();
		const description = (category.categoryDescription || "").toLowerCase();
		const limit = category.categoryLimit;

		const keywordOk = keyword === "" || name.includes(keyword) || description.includes(keyword);
		const colorOk = selectedColors.length === 0 || selectedColors.includes(category.categoryColor);
		const minOk = amountMin === null || (limit !== null && limit !== undefined && limit >= amountMin);
		const maxOk = amountMax === null || (limit !== null && limit !== undefined && limit <= amountMax);

		return keywordOk && colorOk && minOk && maxOk;
	});
}

// Initialisiert die komplette Kategorien-Seite nach dem Laden des DOM.
document.addEventListener("DOMContentLoaded", () => {
	let allCategories = [];
	let editingCategoryId = null;

	const picker = document.getElementById("color-picker");
	const hidden = document.getElementById("category-color");
	const filterForm = document.getElementById("category-filter-form");
	const exportCsvBtn = document.getElementById("category-export-csv-btn");
	const addCategoryForm = document.getElementById("add-category-form");
	const addCategoryButton = document.getElementById("add-category-btn");
	const cancelEditButton = document.getElementById("cancel-edit-category-btn");
	const formModeInfo = document.getElementById("category-form-mode");
	const categoryList = document.getElementById("filtered-categories-list");
	let isProgrammaticReset = false;

	// Synchronisiert die aktuell ausgewaehlte Farbe zwischen Hidden-Input und Radiobuttons.
	const syncSelectedColor = (colorValue) => {
		hidden.value = colorValue || "";
		document.querySelectorAll("input[name='colorChoice']").forEach((input) => {
			input.checked = input.value === colorValue;
		});
	};

	// Beendet den Bearbeitungsmodus und setzt die Formularsteuerung auf "Add" zurueck.
	const leaveEditMode = () => {
		syncSelectedColor("");
		editingCategoryId = null;
		addCategoryButton.textContent = "Add";
		cancelEditButton.hidden = true;
		formModeInfo.hidden = true;
	};

	// Setzt das Formular vollstaendig zurueck und verhindert dabei doppelte Reset-Nachbearbeitung.
	const resetCategoryForm = () => {
		isProgrammaticReset = true;
		addCategoryForm.reset();
		leaveEditMode();
		isProgrammaticReset = false;
	};

	// Rendert die Kategorienliste immer auf Basis des aktuellen Filterzustands.
	const renderCurrentCategories = () => {
		renderCategories(applyCategoryFilters(allCategories));
	};

	// Befuellt das Formular mit den Daten einer vorhandenen Kategorie und aktiviert den Bearbeitungsmodus.
	const startEditCategory = (categoryId) => {
		const category = allCategories.find((entry) => entry.categoryId === categoryId);
		if (!category) {
			alert("Category could not be found.");
			return;
		}

		editingCategoryId = categoryId;
		document.getElementById("category-name").value = category.categoryName || "";
		document.getElementById("category-description").value = category.categoryDescription || "";
		document.getElementById("category-limit").value = category.categoryLimit ?? "";
		syncSelectedColor(category.categoryColor || "");
		addCategoryButton.textContent = "Save";
		cancelEditButton.hidden = false;
		formModeInfo.hidden = false;
		window.scrollTo({ top: 0, behavior: "smooth" });
	};

	// Laedt alle Kategorien vom Backend und zeigt sie direkt mit den aktiven Filtern an.
	const loadAndRenderAllCategories = async () => {
		const categories = await getAllCategories();
		allCategories = Array.isArray(categories) ? categories : [];
		renderCurrentCategories();
	};

	// Loescht eine Kategorie nach Benutzerbestaetigung und aktualisiert anschliessend die Liste.
	const handleDeleteCategory = async (categoryId) => {
		const category = allCategories.find((entry) => entry.categoryId === categoryId);
		if (!category) {
			return;
		}

		const confirmed = window.confirm("Delete category '" + category.categoryName + "'?");
		if (!confirmed) {
			return;
		}

		const result = await deleteCategory(categoryId);
		if (result) {
			if (editingCategoryId === categoryId) {
				resetCategoryForm();
			}
			await loadAndRenderAllCategories();
		}
	};

	// Baut den Farbpicker fuer das Formular auf.
	CATEGORY_COLORS.forEach(({ id, hex, label }) => {
		const input = document.createElement("input");
		input.type = "radio";
		input.id = "color-" + id;
		input.name = "colorChoice";
		input.value = hex;
		input.addEventListener("change", () => hidden.value = hex);

		const circle = createColorElement("label", "color-circle", hex, label);
		circle.htmlFor = "color-" + id;

		picker.appendChild(input);
		picker.appendChild(circle);
	});

	// Baut die Farbfilter fuer die Suchmaske auf.
	const filterGrid = document.getElementById("filter-color-grid");
	if (filterGrid) {
		CATEGORY_COLORS.forEach(({ id, hex, label }) => {
			const item = document.createElement("label");
			item.className = "filter-color-item";

			const checkbox = document.createElement("input");
			checkbox.type = "checkbox";
			checkbox.id = "filter-color-" + id;
			checkbox.name = "categoryColors";
			checkbox.value = hex;

			const dot = createColorElement("span", "filter-color-dot", hex, label);

			item.appendChild(checkbox);
			item.appendChild(dot);
			item.appendChild(document.createTextNode(label));
			filterGrid.appendChild(item);
		});
	}

	// Verarbeitet Suche und Reset in der Filtermaske.
	if (filterForm) {
		filterForm.addEventListener("submit", (event) => {
			event.preventDefault();
			renderCurrentCategories();
		});

		filterForm.addEventListener("reset", () => {
			window.setTimeout(() => renderCategories(allCategories), 0);
		});
	}

	// Zeigt bis zur echten Implementierung einen Platzhalter fuer den CSV-Export an.
	if (exportCsvBtn) {
		exportCsvBtn.addEventListener("click", () => {
			alert("CSV export is not implemented yet.");
		});
	}

	// Bricht den Bearbeitungsmodus manuell ueber den Formularbutton ab.
	if (cancelEditButton) {
		cancelEditButton.addEventListener("click", () => {
			resetCategoryForm();
		});
	}

	// Sorgt dafuer, dass ein normaler Formular-Reset ebenfalls den Bearbeitungsmodus verlaesst.
	if (addCategoryForm) {
		addCategoryForm.addEventListener("reset", () => {
			if (isProgrammaticReset) {
				return;
			}

			window.setTimeout(() => {
				leaveEditMode();
				syncSelectedColor("");
			}, 0);
		});
	}

	// Reagiert zentral auf Klicks auf Edit- und Delete-Buttons innerhalb der Kartenliste.
	if (categoryList) {
		categoryList.addEventListener("click", async (event) => {
			const button = event.target.closest("button[data-action]");
			if (!button) {
				return;
			}

			const categoryId = Number(button.dataset.categoryId);
			if (!Number.isInteger(categoryId)) {
				return;
			}

			if (button.dataset.action === "edit") {
				startEditCategory(categoryId);
				return;
			}

			if (button.dataset.action === "delete") {
				try {
					await handleDeleteCategory(categoryId);
				} catch (error) {
					alert(error.message || "Category could not be deleted.");
				}
			}
		});
	}

	// Erstellt neue Kategorien oder speichert Aenderungen an einer bestehenden Kategorie.
	document.getElementById("add-category-btn").addEventListener("click", async () => {
		const dto = getCategoryFormValues();
		const name  = dto.categoryName;
		const color = dto.categoryColor;

		if (!name) {
			alert("Please enter a category name.");
			return;
		}
		if (!color) {
			alert("Please select a color.");
			return;
		}

		try {
			const wasEditing = editingCategoryId !== null;
			const result = editingCategoryId === null
				? await createCategory(dto)
				: await updateCategory(editingCategoryId, dto);

			if (result) {
				resetCategoryForm();
				await loadAndRenderAllCategories();
				alert(wasEditing ? "Category was updated successfully." : "Category was created successfully.");
			}
		} catch (error) {
			alert(error.message || "Category could not be saved.");
		}
	});

	// Laedt zum Start alle Kategorien und zeigt bei Fehlern einen leeren Zustand an.
	loadAndRenderAllCategories().catch(() => {
		renderCategories([]);
		alert("Categories could not be loaded.");
	});
});