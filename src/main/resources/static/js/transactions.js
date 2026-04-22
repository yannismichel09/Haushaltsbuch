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
    const addBtn = document.getElementById("add-transaction-btn");
    const cancelBtn = document.getElementById("cancel-edit-transaction-btn");
    const resetBtn = document.getElementById("reset-transaction-btn"); 
    const addForm = document.getElementById("add-transaction-form");
    const listContainer = document.getElementById("filtered-transactions-list");
    
    if (resetBtn) {
        resetBtn.addEventListener("click", () => {
            if (addForm) addForm.reset();
            leaveEditMode();
            const spendingRadio = document.querySelector('input[name="transaction-type"][value="spending"]');
            if (spendingRadio) spendingRadio.checked = true;
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

            console.log("Aktion:", action, "für ID:", tId);

            if (tId === "undefined" || !tId) {
                console.error("Fehler: Diese Transaktion hat keine gültige ID!");
                return;
            }

            if (action === "edit") {
                startEditTransaction(tId);
            } else if (action === "delete") {
                await handleDeleteTransaction(tId);
            }
        });
    }

    await initializePage();
});

function leaveEditMode() {
    editingTransactionId = null;
    const addBtn = document.getElementById("add-transaction-btn");
    const cancelBtn = document.getElementById("cancel-edit-transaction-btn");
    
    if (addBtn) addBtn.textContent = "Add";
    if (cancelBtn) cancelBtn.hidden = true;
    
    const selects = ["payed-by-select", "category-select", "frequency"];
    selects.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.selectedIndex = 0;
    });
}

function startEditTransaction(transactionId) {
    if (!transactionId) return;

    const t = allTransactions.find((item) => {
        const itemId = String(item.transactionId || item.id);
        return itemId === String(transactionId);
    });

    if (!t) {
        console.error("Transaktion nicht gefunden", transactionId);
        return;
    }

    editingTransactionId = transactionId;

    if (document.getElementById("desc")) document.getElementById("desc").value = t.transactionDescription || "";
    if (document.getElementById("amount")) document.getElementById("amount").value = t.transactionAmount || "";
    if (document.getElementById("date")) document.getElementById("date").value = t.transactionDate || "";
    if (document.getElementById("category-select")) document.getElementById("category-select").value = t.categoryId || "";
    if (document.getElementById("frequency")) document.getElementById("frequency").value = t.transactionFrequency || "";
    
    const userSelect = document.getElementById("payed-by-select");
    if (userSelect) userSelect.value = String(t.userId);
    
    const radio = document.querySelector(`input[name="transaction-type"][value="${t.transactionType}"]`);
    if (radio) radio.checked = true;

    const addBtn = document.getElementById("add-transaction-btn");
    const cancelBtn = document.getElementById("cancel-edit-transaction-btn");
    
    if (addBtn) addBtn.textContent = "Save Changes";
    if (cancelBtn) cancelBtn.hidden = false;

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
    } catch (e) { console.error("Fehler bei Init:", e); }
}

async function loadAllTransactions() {
    try {
        allTransactions = await getTransactions();
        renderTransactions(allTransactions);
    } catch (e) { console.error("Fehler beim Laden:", e); }
}

async function handleAddTransaction() {
    const desc = document.getElementById("desc")?.value;
    const amount = parseFloat(document.getElementById("amount")?.value);
    const selectedUserId = document.getElementById("payed-by-select")?.value;

    const dto = {
        userId: parseInt(selectedUserId),
        categoryId: parseInt(document.getElementById("category-select")?.value),
        transactionAmount: amount,
        transactionDate: document.getElementById("date")?.value,
        transactionType: document.querySelector('input[name="transaction-type"]:checked')?.value,
        transactionDescription: desc,
        transactionFrequency: document.getElementById("frequency")?.value
    };

    if (!desc || isNaN(amount) || !dto.userId) {
        alert("Please fill out all fields and select a user!");
        return;
    }

    try {
        if (editingTransactionId) {
            await updateTransaction(editingTransactionId, dto);
        } else {
            await createTransaction(dto);
        }
        document.getElementById("add-transaction-form")?.reset();
        leaveEditMode();
        await loadAllTransactions();
    } catch (e) { alert("Error: " + e.message); }
}

async function handleDeleteTransaction(tId) {
    if (!confirm("Are you sure you want to delete this transaction?")) return;
    try {
        await deleteTransaction(tId);
        await loadAllTransactions();
    } catch (e) { alert(e.message); }
}

function renderTransactions(transactions) {
    const container = document.getElementById("filtered-transactions-list");
    if (!container) return;
    container.innerHTML = "";

    transactions.forEach((t) => {
        const currentId = t.transactionId || t.id;
        const card = document.createElement("article");
        
        card.className = "transaction-card"; 
        
        const cat = allCategories.find(c => c.categoryId == t.categoryId);
        const catName = cat ? (cat.categoryName || cat.name) : "N/A";
        const user = allUsers.find(u => u.userId == t.userId);
        const userName = user ? (user.username || user.name) : "N/A";
        
        const typeLabel = t.transactionType === "income" ? "Income" : "Spending";
        const typeClass = t.transactionType === "income" ? "type-income" : "type-spending";

        card.innerHTML = `
            <div class="card-header">
                <h3>${t.transactionDescription || "No Description"}</h3>
                <span class="type-badge ${typeClass}">${typeLabel}</span>
            </div>
            <p><strong>Date:</strong> ${t.transactionDate || "-"}</p>
            <p><strong>Category:</strong> ${catName}</p>
            <p><strong>User:</strong> ${userName}</p>
            <div class="transaction-amount">${formatAmount(t.transactionAmount)}</div>
            <div class="category-card-actions">
                <button type="button" class="btn-action" style="background-color: #2c3e50; color: white; padding: 6px 12px;" 
                        data-action="edit" data-id="${currentId}">Edit</button>
                <button type="button" class="transaction-card-button-delete" 
                        data-action="delete" data-id="${currentId}">Delete</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function formatAmount(amount) {
    return (amount === null || amount === undefined || Number.isNaN(amount)) 
        ? "-" 
        : new Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(amount);
}

function fillCategoryDropdowns(data) {
    const addSelect = document.getElementById("category-select");
    if (addSelect) {
        addSelect.innerHTML = '<option value="" disabled selected>Select...</option>';
        data.forEach(cat => {
            const option = new Option(cat.categoryName || cat.name, cat.categoryId);
            addSelect.appendChild(option);
        });
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