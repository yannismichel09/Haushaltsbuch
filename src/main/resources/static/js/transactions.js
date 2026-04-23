let allTransactions = [];
let allCategories = []; 
let allUsers = [];     
let editingTransactionId = null;

function getDynamicUserId() {
    const userString = localStorage.getItem('currentUser');
    if (!userString) return 1;
    try {
        const user = JSON.parse(userString);
        return user.userId;
    } catch (e) {
        console.error("Fehler beim Parsen der User-Daten:", e);
        return 1;
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    const exportBtn = document.getElementById("transaction-export-csv-btn");
    
    if (exportBtn) {
        exportBtn.addEventListener("click", async () => {
            console.log("CSV Button geklickt!"); 
            try {
                const filters = getTransactionFilterValues();
                await exportFilteredTransactionsToCsv(filters);
            } catch (e) {
                alert("CSV-Export fehlgeschlagen: " + e.message);
            }
        });
    }

    const addBtn = document.getElementById("add-transaction-btn");
    const cancelBtn = document.getElementById("cancel-edit-transaction-btn");
    const addForm = document.getElementById("add-transaction-form");
    const listContainer = document.getElementById("filtered-transactions-list");
    
    const filterForm = document.querySelector(".search-filter form");
    if (filterForm) {
        filterForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            await handleFilter();
        });
    }

    if (addBtn) addBtn.addEventListener("click", handleAddTransaction);

    if (cancelBtn) {
        cancelBtn.addEventListener("click", () => {
            if (addForm) addForm.reset();
            leaveEditMode();
        });
    }

    if (listContainer) {
        listContainer.addEventListener("click", async (event) => {
            const btn = event.target.closest("button[data-action]");
            if (!btn) return;
            const tId = btn.dataset.id; 
            const action = btn.dataset.action;
            if (tId === "undefined" || !tId) return;
            if (action === "edit") {
                startEditTransaction(tId);
            } else if (action === "delete") {
                await handleDeleteTransaction(tId);
            }
        });
    }

    await initializePage();
});

function getTransactionFilterValues() {
    return {
        transactionId: null,
        userId: null,       
        categoryId: document.getElementById("filter-category").value ? parseInt(document.getElementById("filter-category").value) : null,
        amountMin: document.getElementById("filter-amount-from").value !== "" ? parseFloat(document.getElementById("filter-amount-from").value) : null,
        amountMax: document.getElementById("filter-amount-to").value !== "" ? parseFloat(document.getElementById("filter-amount-to").value) : null,
        transactionDateFrom: document.getElementById("filter-date-from").value || null,
        transactionDateTo: document.getElementById("filter-date-to").value || null,
        transactionType: document.getElementById("filter-type").value || null, 
        keyword: document.getElementById("search-input").value.trim() || null,       
        transactionFrequency: document.getElementById("filter-frequency").value || null
    };
}

async function handleFilter() {
    const filterDto = getTransactionFilterValues();
    try {
        const filteredData = await getFilteredTransactions(filterDto);
        renderTransactions(filteredData || []);
    } catch (e) {
        console.error("Fehler beim Filtern:", e);
    }
}

function leaveEditMode() {
    editingTransactionId = null;
    const addBtn = document.getElementById("add-transaction-btn");
    const cancelBtn = document.getElementById("cancel-edit-transaction-btn");
    if (addBtn) addBtn.textContent = "Add";
    if (cancelBtn) cancelBtn.hidden = true;
}

function startEditTransaction(transactionId) {
    const t = allTransactions.find((item) => String(item.transactionId || item.id) === String(transactionId));
    if (!t) return;
    editingTransactionId = transactionId;
    document.getElementById("desc").value = t.transactionDescription || "";
    document.getElementById("amount").value = t.transactionAmount || "";
    document.getElementById("date").value = t.transactionDate || "";
    document.getElementById("category-select").value = t.categoryId || "";
    document.getElementById("frequency").value = t.transactionFrequency || "";
    document.getElementById("payed-by-select").value = String(t.userId);
    const radio = document.querySelector(`input[name="transaction-type"][value="${t.transactionType}"]`);
    if (radio) radio.checked = true;
    document.getElementById("add-transaction-btn").textContent = "Save Changes";
    document.getElementById("cancel-edit-transaction-btn").hidden = false;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function initializePage() {
    try {
        const [categories, users] = await Promise.all([getAllCategories(), getAllUsers()]);
        allCategories = Array.isArray(categories) ? categories : (categories.categories || []);
        allUsers = users || [];
        fillCategoryDropdowns(allCategories);
        fillUserDropdown(allUsers);
        await loadAllTransactions();
    } catch (e) { console.error(e); }
}

async function loadAllTransactions() {
    try {
        allTransactions = await getTransactions();
        renderTransactions(allTransactions);
    } catch (e) { console.error(e); }
}

async function handleAddTransaction() {
    const dto = {
        userId: parseInt(document.getElementById("payed-by-select").value),
        categoryId: parseInt(document.getElementById("category-select").value),
        transactionAmount: parseFloat(document.getElementById("amount").value),
        transactionDate: document.getElementById("date").value,
        transactionType: document.querySelector('input[name="transaction-type"]:checked').value,
        transactionDescription: document.getElementById("desc").value,
        transactionFrequency: document.getElementById("frequency").value
    };
    if (!dto.transactionDescription || isNaN(dto.transactionAmount)) {
        alert("Please fill out all fields!");
        return;
    }
    try {
        if (editingTransactionId) {
            await updateTransaction(editingTransactionId, dto);
        } else {
            await createTransaction(dto);
        }
        document.getElementById("add-transaction-form").reset();
        leaveEditMode();
        await loadAllTransactions();
    } catch (e) { alert(e.message); }
}

async function handleDeleteTransaction(tId) {
    if (!confirm("Are you sure?")) return;
    try {
        await deleteTransaction(tId);
        await loadAllTransactions();
    } catch (e) { alert(e.message); }
}

function renderTransactions(transactions) {
    const container = document.getElementById("filtered-transactions-list");
    if (!container) return;
    container.innerHTML = "";

    if (!transactions || transactions.length === 0) {
        container.innerHTML = `
            <div class="transactions-empty-state">
                No transactions found matching your filters.
            </div>
        `;
        return;
    }

    transactions.forEach((t) => {
        const currentId = t.transactionId || t.id;
        const card = document.createElement("article");
        card.className = "transaction-card"; 
        const cat = allCategories.find(c => c.categoryId == t.categoryId);
        const user = allUsers.find(u => u.userId == t.userId);
        const typeLabel = t.transactionType === "income" ? "Income" : "Spending";
        const typeClass = t.transactionType === "income" ? "type-income" : "type-spending";
        
        card.innerHTML = `
            <div class="card-header">
                <h3 style="margin:0; font-size: 1.1rem;">${t.transactionDescription || "No Description"}</h3>
                <span class="type-badge ${typeClass}">${typeLabel}</span>
            </div>
            <p style="margin: 8px 0 4px 0;"><strong>Date:</strong> ${t.transactionDate || "-"}</p>
            <p style="margin: 4px 0;"><strong>Category:</strong> ${cat ? (cat.categoryName || cat.name) : "N/A"}</p>
            <p style="margin: 4px 0;"><strong>User:</strong> ${user ? (user.username || user.name) : "N/A"}</p>
            <div class="transaction-amount">${formatAmount(t.transactionAmount)}</div>
            <div class="category-card-actions">
                <button type="button" class="btn-action" style="background-color: #2c3e50; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;" 
                        data-action="edit" data-id="${currentId}">Edit</button>
                <button type="button" class="transaction-card-button-delete" 
                        data-action="delete" data-id="${currentId}">Delete</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function formatAmount(amount) {
    return new Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(amount || 0);
}

function fillCategoryDropdowns(data) {
    const addSelect = document.getElementById("category-select");
    const filterSelect = document.getElementById("filter-category");
    if (addSelect) {
        addSelect.innerHTML = '<option value="" disabled selected>Select...</option>';
        data.forEach(cat => addSelect.appendChild(new Option(cat.categoryName || cat.name, cat.categoryId)));
    }
    if (filterSelect) {
        filterSelect.innerHTML = '<option value="" selected>All</option>';
        data.forEach(cat => filterSelect.appendChild(new Option(cat.categoryName || cat.name, cat.categoryId)));
    }
}

function fillUserDropdown(users) {
    const userSelect = document.getElementById("payed-by-select");
    if (!userSelect) return;
    const myId = getDynamicUserId();
    userSelect.innerHTML = '<option value="" disabled selected>Select...</option>';
    const me = users.find(u => u.userId == myId);
    if (me) {
        userSelect.appendChild(new Option(`👤 ${me.username || me.name} (Me)`, me.userId));
    }
    users.forEach(u => {
        if (u.userId != myId) {
            userSelect.appendChild(new Option(u.username || u.name, u.userId));
        }
    });
}