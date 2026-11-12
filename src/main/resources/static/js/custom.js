function togglePassword(fieldId, button) {
    const input = document.getElementById(fieldId);
    const icon = button.querySelector("i");

    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove("bi-eye");
        icon.classList.add("bi-eye-slash");
    } else {
        input.type = "password";
        icon.classList.remove("bi-eye-slash");
        icon.classList.add("bi-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const alert = document.getElementById("floatingAlert");

    if (alert) {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 5000);
    }
});

//SEARCH FUNCTION
function searchTable(searchId, tableId) {
    const input = document.getElementById(searchId);
    const table = document.getElementById(tableId);

    input.addEventListener("keyup", function () {
        let search = this.value.toLowerCase();
        let rows = table.querySelectorAll("tbody tr");
        let visibleCount = 0;

        rows.forEach(function (row) {
            // skip the "no results" row itself
            if (row.classList.contains("no-results")) return;

            let text = row.innerText.toLowerCase();
            let match = text.includes(search);
            
            row.style.display = match ? "" : "none";
            if (match) visibleCount++;
        });

        // Handle "No results" row
        let noResultRow = table.querySelector(".no-results");
        if (!noResultRow) {
            // create it if it doesn't exist
            noResultRow = document.createElement("tr");
            noResultRow.classList.add("no-results");
            let td = document.createElement("td");
            td.colSpan = rows[0] ? rows[0].children.length : 3; // auto set columns
            td.classList.add("text-center", "text-muted", "fw-bold", "py-3");
            td.innerText = "No results found";
            noResultRow.appendChild(td);
            table.querySelector("tbody").appendChild(noResultRow);
        }
        
        noResultRow.style.display = visibleCount === 0 ? "" : "none";
    });
}

//Format to SenteceCase
function formatSentenceCase(input) {
    let value = input.value.toLowerCase();

    // Capitalize the first letter of the text
    value = value.replace(/^\s*[a-z]/, function (match) {
        return match.toUpperCase();
    });

    // Capitalize the first letter after . ! or ?
    value = value.replace(/([.!?]\s+)([a-z])/g, function (match, punctuation, letter) {
        return punctuation + letter.toUpperCase();
    });

    input.value = value;
}