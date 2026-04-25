let allCategories = [];
let monthlyTrendChart = null;

function formatCurrency(amount) {
    return amount.toFixed(2).replace(".", ",") + " €";
}

function getCategoryName(cat) {
    return cat.categoryName || cat.name;
}

function isCurrentMonthTransaction(transactionDate) {
    if (!transactionDate) return false;

    const date = new Date(transactionDate);
    if (Number.isNaN(date.getTime())) return false;

    const now = new Date();
    return date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth();
}

document.addEventListener("DOMContentLoaded", async () => {
    const filterForm = document.querySelector(".search-filter form") || document.querySelector("section form");
    const resultsSection = document.getElementById("search-results-section");
    const exportBtn = document.getElementById("dashboard-export-csv-btn");

    if (filterForm) {
        filterForm.addEventListener("submit", async (e) => {
            e.preventDefault(); 
            await handleDashboardFilter();
        });

        filterForm.addEventListener("reset", () => {
            if (resultsSection) resultsSection.style.display = "none";
            const list = document.getElementById("filtered-transactions-list");
            if (list) list.innerHTML = "";
        });
    }

    if (exportBtn) {
        exportBtn.addEventListener("click", async () => {
            try {
                const filters = getDashboardFilterValues();
                await exportFilteredTransactionsToCsv(filters);
            } catch (e) {
                alert("CSV-Export fehlgeschlagen: " + e.message);
            }
        });
    }

    await initDashboard();
});

function getDashboardFilterValues() {
    return {
        keyword: document.getElementById("search-input").value.trim() || null,
        transactionType: document.getElementById("filter-type").value || null,
        categoryId: document.getElementById("filter-category").value ? parseInt(document.getElementById("filter-category").value) : null,
        transactionFrequency: document.getElementById("filter-frequency").value || null,
        transactionDateFrom: document.getElementById("filter-date-from").value || null,
        transactionDateTo: document.getElementById("filter-date-to").value || null,
        amountMin: document.getElementById("filter-amount-from").value !== "" ? parseFloat(document.getElementById("filter-amount-from").value) : null,
        amountMax: document.getElementById("filter-amount-to").value !== "" ? parseFloat(document.getElementById("filter-amount-to").value) : null
    };
}

async function initDashboard() {
    try {
        // Load categories
        const categories = await getAllCategories();
        allCategories = Array.isArray(categories) ? categories : (categories.categories || []);
        fillCategoryDropdown(allCategories);

        // Load transactions once and reuse for chart + budget views
        const transactions = await getAllDashboardTransactions() || [];
        const currentMonthTransactions = transactions.filter(t => isCurrentMonthTransaction(t.transactionDate));

        // Load summary data for current month
        await loadSummaryData(currentMonthTransactions);

        // Load and render monthly trend for the whole current year
        await loadMonthlyTrendChart(transactions);

        // Load and render budget vs actual for current month
        await loadBudgetVsActual(currentMonthTransactions);
    } catch (e) {
        console.error("Fehler beim Initialisieren des Dashboards:", e);
    }
}

async function loadSummaryData(transactions) {
    try {
        const income = transactions
            .filter(t => t.transactionType === "income")
            .reduce((sum, t) => sum + (t.transactionAmount || 0), 0);

        const spending = transactions
            .filter(t => t.transactionType === "spending")
            .reduce((sum, t) => sum + (t.transactionAmount || 0), 0);

        const balance = income - spending;

        document.getElementById("income-sum").textContent = formatCurrency(income);
        document.getElementById("spending-sum").textContent = formatCurrency(spending);
        document.getElementById("balance-sum").textContent = formatCurrency(balance);
    } catch (e) {
        console.error("Fehler beim Laden der Summary-Daten:", e);
    }
}

async function loadMonthlyTrendChart(transactions) {
    try {
        const now = new Date();
        const currentYear = now.getFullYear();
        const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const monthlyData = Array.from({ length: 12 }, () => ({ income: 0, spending: 0 }));

        // Aggregate current-year transactions by month
        transactions.forEach(t => {
            if (t.transactionDate) {
                try {
                    const date = new Date(t.transactionDate);
                    if (Number.isNaN(date.getTime()) || date.getFullYear() !== currentYear) return;

                    const monthIndex = date.getMonth();
                    const amount = t.transactionAmount || 0;

                    if (t.transactionType === "income") {
                        monthlyData[monthIndex].income += amount;
                    } else {
                        monthlyData[monthIndex].spending += amount;
                    }
                } catch (e) {
                    console.warn("Fehler beim Parsen des Datums:", t.transactionDate);
                }
            }
        });

        // Prepare chart data
        const labels = months;
        const incomeData = monthlyData.map(month => month.income);
        const spendingData = monthlyData.map(month => month.spending);

        // Create or update chart
        const chartCanvas = document.getElementById("monthly-trend-chart");
        if (chartCanvas) {
            // Destroy existing chart if it exists
            if (monthlyTrendChart) {
                monthlyTrendChart.destroy();
            }

            monthlyTrendChart = new Chart(chartCanvas, {
                type: "line",
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "Income",
                            data: incomeData,
                            borderColor: "#27ae60",
                            backgroundColor: "rgba(39, 174, 96, 0.1)",
                            tension: 0.3,
                            fill: true
                        },
                        {
                            label: "Spending",
                            data: spendingData,
                            borderColor: "#e74c3c",
                            backgroundColor: "rgba(231, 76, 60, 0.1)",
                            tension: 0.3,
                            fill: true
                        }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: "top"
                        }
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: "Months"
                            }
                        },
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: "Amount (€)"
                            }
                        }
                    }
                }
            });
        }
    } catch (e) {
        console.error("Fehler beim Laden des Monthly Trend Charts:", e);
    }
}

async function loadBudgetVsActual(transactions) {
    try {
        const container = document.getElementById("budget-vs-actual-container");
        if (!container) return;

        // Group by category
        const categoryBudget = {};
        allCategories.forEach(cat => {
            const rawLimit = Number(cat.categoryLimit);
            const hasLimit = Number.isFinite(rawLimit) && rawLimit > 0;

            categoryBudget[cat.categoryId] = {
                name: getCategoryName(cat),
                actual: 0,
                budget: hasLimit ? rawLimit : null,
                hasLimit: hasLimit
            };
        });

        // Sum spending by category
        transactions.forEach(t => {
            if (t.transactionType === "spending" && categoryBudget[t.categoryId]) {
                categoryBudget[t.categoryId].actual += t.transactionAmount || 0;
            }
        });

        const categories = Object.values(categoryBudget);
        const withLimit = categories.filter(cat => cat.hasLimit);
        const withoutLimit = categories.filter(cat => !cat.hasLimit);

        const withLimitHtml = withLimit.map(cat => {
            const percentage = (cat.actual / cat.budget) * 100;
            const displayPercentage = Math.min(100, percentage);

            return `
                <div style="margin-bottom: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                        <span style="font-weight: bold;">${cat.name}</span>
                        <span>${formatCurrency(cat.actual)} / ${formatCurrency(cat.budget)}</span>
                    </div>
                    <div>
                        <progress value="${displayPercentage}" max="100" style="width: 100%; height: 20px;">${displayPercentage}%</progress>
                    </div>
                    ${percentage > 100 ? `<p style="color: #e74c3c; font-size: 0.85rem; margin: 5px 0 0 0;">⚠ Budget exceeded by ${(percentage - 100).toFixed(0)}%</p>` : ""}
                </div>
            `;
        }).join("");

        const withoutLimitHtml = withoutLimit.map(cat => {
            return `
                <div style="margin-bottom: 12px; padding: 10px 12px; border: 1px solid #e0e0e0; border-radius: 6px; background-color: #fafafa;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: bold;">${cat.name}</span>
                        <span>${formatCurrency(cat.actual)}</span>
                    </div>
                </div>
            `;
        }).join("");

        container.innerHTML = `
            <div style="margin-bottom: 24px;">
                <h3 style="margin: 0 0 12px; color: #2c3e50;">Categories with Limit</h3>
                ${withLimitHtml || '<p style="color: #7f8c8d; margin: 0;">No categories with limit available.</p>'}
            </div>
            <div>
                <h3 style="margin: 0 0 12px; color: #2c3e50;">Categories without Limit</h3>
                ${withoutLimitHtml || '<p style="color: #7f8c8d; margin: 0;">No categories without limit available.</p>'}
            </div>
        `;
    } catch (e) {
        console.error("Fehler beim Laden des Budget vs Actual:", e);
    }
}

async function handleDashboardFilter() {
    const resultsSection = document.getElementById("search-results-section");
    const listContainer = document.getElementById("filtered-transactions-list");
    const filterDto = getDashboardFilterValues();

    try {
        const results = await getFilteredDashboardTransactions(filterDto);
        
        if (results && results.length > 0) {
            renderDashboardResults(results);
            if (resultsSection) resultsSection.style.display = "block";
        } else {
            listContainer.innerHTML = `
                <div class="transactions-empty-state">
                    No transactions found.
                </div>
            `;
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

        const typeBadgeClass = t.transactionType === "income" ? "type-income" : "type-spending";

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
            
            <div style="font-size: 0.85rem; color: #7f8c8d; margin-top: 4px;">
                <strong>Category:</strong> ${cat ? getCategoryName(cat) : 'Category'}
            </div>

            <div style="font-size: 0.85rem; color: #7f8c8d; margin-top: 4px;">
                <strong>User:</strong> ${t.userName || "testUser2"}
            </div>

            <div style="font-size: 0.85rem; color: #7f8c8d; margin-top: 4px;">
                <strong>Frequency:</strong> ${t.transactionFrequency || "once"}
            </div>
            
            <div class="transaction-amount">
                ${formatCurrency(t.transactionAmount)}
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
        select.appendChild(new Option(getCategoryName(c), c.categoryId));
    });
}