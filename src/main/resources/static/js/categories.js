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

		const circle = document.createElement("label");
		circle.htmlFor = "color-" + id;
		circle.className = "color-circle";
		circle.style.backgroundColor = hex;
		circle.title = label;

		picker.appendChild(input);
		picker.appendChild(circle);
	});

	document.getElementById("reset-category-btn").addEventListener("click", () => {
		document.getElementById("category-name").value = "";
		document.getElementById("category-description").value = "";
		document.getElementById("category-limit").value = "";
		hidden.value = "";
		picker.querySelectorAll("input[type='radio']").forEach(r => r.checked = false);
	});
});