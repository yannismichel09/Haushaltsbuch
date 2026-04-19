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

document.addEventListener("DOMContentLoaded", () => {
	const picker = document.getElementById("color-picker");
	const hidden = document.getElementById("category-color");

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
});