let allCategories = [];

document.addEventListener("DOMContentLoaded", async () => {
    const filterForm = document.querySelector(".search-filter form") || document.querySelector("section form");
    const resultsSection = document.getElementById("search-results-section");

    console.log("Formular gefunden:", filterForm); 

    if (filterForm) {
        filterForm.addEventListener("submit", async (e) => {
            e.preventDefault(); 
            console.log("Filter wird ausgeführt...");
            await handleDashboardFilter();
        });

        filterForm.addEventListener("reset", () => {
            if (resultsSection) resultsSection.style.display = "none";
            const list = document.getElementById("filtered-transactions-list");
            if (list) list.innerHTML = "";
        });
    } else {
        console.error("Fehler: Filter-Formular wurde im HTML nicht gefunden!");
    }

    await initDashboard();
});

async function initDashboard() {
    try {
        const categories = await getAllCategories();
        allCategories = Array.isArray(categories) ? categories : (categories.categories || []);
        
        fillCategoryDropdown(allCategories);
        
    } catch (e) {
        console.error("Fehler beim Initialisieren des Dashboards:", e);
    }
}

async function handleDashboardFilter() {
    const resultsSection = document.getElementById("search-results-section");
    const listContainer = document.getElementById("filtered-transactions-list");

    const filterDto = {
        keyword: document.getElementById("search-input").value.trim() || null,
        transactionType: document.getElementById("filter-type").value || null,
        categoryId: document.getElementById("filter-category").value ? parseInt(document.getElementById("filter-category").value) : null,
        transactionFrequency: document.getElementById("filter-frequency").value || null,
        transactionDateFrom: document.getElementById("filter-date-from").value || null,
        transactionDateTo: document.getElementById("filter-date-to").value || null,
        amountMin: document.getElementById("filter-amount-from").value !== "" ? parseFloat(document.getElementById("filter-amount-from").value) : null,
        amountMax: document.getElementById("filter-amount-to").value !== "" ? parseFloat(document.getElementById("filter-amount-to").value) : null
    };

    try {
        const results = await getFilteredDashboardTransactions(filterDto);
        
        if (results && results.length > 0) {
            renderDashboardResults(results);
            if (resultsSection) resultsSection.style.display = "block";
        } else {
            listContainer.innerHTML = "<p>No matching transactions found.</p>";
            if (resultsSection) resultsSection.style.display = "block";
        }
    } catch (e) {
        console.error("Fehler beim Filtern im Dashboard:", e);
    }
}

function renderDashboardResults(transactions) {
    const container = document.getElementById("filtered-transactions-list");
    container.innerHTML = "";

    transactions.forEach(t => {
        const cat = allCategories.find(c => c.categoryId == t.categoryId);
        const card = document.createElement("div");
        
        card.className = "transaction-card"; 

        const isIncome = t.transactionType === "income";
        const typeBadgeClass = isIncome ? "type-income" : "type-spending";

        card.innerHTML = `
            <div class="card-header">
                <strong class="transaction-description">${t.transactionDescription || "No Description"}</strong>
                <span class="type-badge ${typeBadgeClass}">
                    ${t.transactionType}
                </span>
            </div>
            
            <div class="transaction-date">
                <strong>Date:</strong> ${t.transactionDate}
            </div>
            
            <div class="transaction-category" style="font-size: 0.85rem; color: #7f8c8d; margin-top: 4px;">
                <strong>Category:</strong> ${cat ? (cat.categoryName || cat.name) : 'Category'}
            </div>

            <div class="transaction-user" style="font-size: 0.85rem; color: #7f8c8d; margin-top: 4px;">
                <strong>User:</strong> ${t.userName || "unknown user"}
            </div>
            
            <div class="transaction-amount">
                ${t.transactionAmount.toFixed(2)} €
            </div>
        `;
        
        container.appendChild(card);
    });
}

function fillCategoryDropdown(categories) {
    const select = document.getElementById("filter-category");
    if (!select) return;
    select.innerHTML = '<option value="" selected>All</option>';
    categories.forEach(c => {
        select.appendChild(new Option(c.categoryName || c.name, c.categoryId));
    });
}