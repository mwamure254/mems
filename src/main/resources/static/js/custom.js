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

//get geolocation
let mapNew, markerNew;
function getLocation(id) {
    const lat = id === 'New' ? 'latNew' : 'lat' + id;
    const lng = id === 'New' ? 'lngNew' : 'lng' + id;
    const map = id === 'New' ? 'mapNew' : 'map' + id;

    const latInput = document.getElementById(lat);
    const lngInput = document.getElementById(lng);
    const mapDiv = document.getElementById(map);

    if (!navigator.geolocation) {
        alert("Geolocation is not supported by this browser.");
        return;
    }

    navigator.geolocation.getCurrentPosition(
        (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;

            // 1. Fill inputs
            latInput.value = lat.toFixed(6);
            lngInput.value = lng.toFixed(6);

            // 2. Init or update Leaflet map
            if (!mapNew) {
                mapNew = L.map(mapDiv).setView([lat, lng], 17); // 17 = zoom close
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap'
                }).addTo(mapNew);
            } else {
                mapNew.setView([lat, lng], 17);
            }

            // 3. Add/Update marker
            if (markerNew) {
                markerNew.setLatLng([lat, lng]);
            } else {
                markerNew = L.marker([lat, lng]).addTo(mapNew)
                    .bindPopup("You are here").openPopup();
            }

            // 4. Show accuracy circle 500m
            L.circle([lat, lng], {
                color: 'blue',
                fillColor: '#30f',
                fillOpacity: 0.1,
                radius: 500
            }).addTo(mapNew);

        },
        (error) => {
            let msg = "";
            switch(error.code) {
                case error.PERMISSION_DENIED: msg = "Permission denied. Please allow location access."; break;
                case error.POSITION_UNAVAILABLE: msg = "Location information unavailable."; break;
                case error.TIMEOUT: msg = "Request timed out."; break;
                default: msg = "Unknown error";
            }
            alert(msg);
        },
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
}

//calculate distance