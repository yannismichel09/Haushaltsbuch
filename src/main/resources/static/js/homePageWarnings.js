document.addEventListener("DOMContentLoaded", async () => {
    await initWarnings();
});

async function initWarnings() {
    const container = document.getElementById("warning-container");
    const list = document.getElementById("warning-list");
    
    if (!container || !list) return;

    let allWarnings = [];

    try {
        const budgetLimits = await checkBudgetLimit();
        if (budgetLimits && budgetLimits.length > 0) {
            budgetLimits.forEach(cat => {
                if(cat.categoryLimit != null){
                    allWarnings.push(`Budget for <strong>${cat.categoryName || cat.name}</strong> is over 90% exhausted!`);
                }
            });
        }

        const netBalanceData = await checkNetBalanceNegative();
        if (netBalanceData === true || (netBalanceData && netBalanceData.isNegative)) {
            allWarnings.push(`Warning: Your spending currently exceed your income!`);
        }

        if (allWarnings.length > 0) {
            list.innerHTML = allWarnings.map(w => `<li>${w}</li>`).join("");
            container.classList.remove("hidden");
        } else {
            container.classList.add("hidden");
        }

    } catch (error) {
        console.error("Fehler beim Laden der Warnungen:", error);
    }
}