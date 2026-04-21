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

function createColorElement(tag, className, hex, title) {
	const el = document.createElement(tag);
	el.className = className;
	el.style.backgroundColor = hex;
	el.title = title;
	return el;
}

function formatAmount(amount) {
	if (amount === null || amount === undefined || Number.isNaN(amount)) {
		return "-";
	}

	return new Intl.NumberFormat("de-DE", {
		style: "currency",
		currency: "EUR"
	}).format(amount);
}

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

		card.appendChild(titleRow);
		card.appendChild(description);
		card.appendChild(limit);
		fragment.appendChild(card);
	});

	container.appendChild(fragment);
}

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

document.addEventListener("DOMContentLoaded", () => {
	let allCategories = [];

	const picker = document.getElementById("color-picker");
	const hidden = document.getElementById("category-color");
	const filterForm = document.getElementById("category-filter-form");
	const exportCsvBtn = document.getElementById("category-export-csv-btn");

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

	const loadAndRenderAllCategories = async () => {
		const categories = await getAllCategories();
		allCategories = Array.isArray(categories) ? categories : [];
		renderCategories(allCategories);
	};

	if (filterForm) {
		filterForm.addEventListener("submit", (event) => {
			event.preventDefault();
			renderCategories(applyCategoryFilters(allCategories));
		});

		filterForm.addEventListener("reset", () => {
			window.setTimeout(() => renderCategories(allCategories), 0);
		});
	}

	if (exportCsvBtn) {
		exportCsvBtn.addEventListener("click", () => {
			alert("CSV export is not implemented yet.");
		});
	}

	document.getElementById("add-category-btn").addEventListener("click", async () => {
		const name  = document.getElementById("category-name").value.trim();
		const color = hidden.value;

		if (!name) {
			alert("Please enter a category name.");
			return;
		}
		if (!color) {
			alert("Please select a color.");
			return;
		}

		const limitRaw = document.getElementById("category-limit").value;
		const dto = {
			categoryName:        name,
			categoryDescription: document.getElementById("category-description").value.trim(),
			categoryColor:       color,
			categoryLimit:       limitRaw !== "" ? parseFloat(limitRaw) : null,
		};

		try {
			const result = await createCategory(dto);
			if (result) {
				document.getElementById("add-category-form").reset();
				hidden.value = "";
				await loadAndRenderAllCategories();
				alert("Category was created successfully.");
			}
		} catch (error) {
			alert(error.message || "Category could not be created.");
		}
	});

	loadAndRenderAllCategories().catch(() => {
		renderCategories([]);
		alert("Categories could not be loaded.");
	});
});