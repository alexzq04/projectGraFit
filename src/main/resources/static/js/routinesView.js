(() => {
    'use strict';

    // Bootstrap validation
    document.querySelectorAll('.needs-validation').forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Sort by day of week, both initial and on click
    const order = ['LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SÁBADO', 'DOMINGO'];
    const table = document.getElementById('routinesTable');
    if (table) {
        const header = table.querySelector('th[data-day-sort]');
        const tbody = table.tBodies[0];
        let asc = true;

        function sortRows() {
            const rows = Array.from(tbody.rows);
            rows.sort((a, b) => {
                const da = a.cells[2].innerText.trim().toUpperCase();
                const db = b.cells[2].innerText.trim().toUpperCase();
                const ia = order.indexOf(da);
                const ib = order.indexOf(db);
                return asc ? ia - ib : ib - ia;
            });
            rows.forEach(r => tbody.appendChild(r));
        }

        // initial sort ascending
        sortRows();
        asc = false; // next click will reverse

        // on header click toggle
        header.addEventListener('click', () => {
            asc = !asc;
            sortRows();
        });
    }

    // Función para actualizar los campos de series
    window.updateSetInputs = function (numberOfSets) {
        const container = document.getElementById('setDetails');
        container.innerHTML = '';

        for (let i = 0; i < numberOfSets; i++) {
            const setDiv = document.createElement('div');
            setDiv.className = 'row g-3 mb-3 align-items-end';
            setDiv.innerHTML = `
                <div class="col-12">
                    <h6 class="mb-3">Serie ${i + 1}</h6>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Repeticiones</label>
                    <input type="number" name="repetitions[]" class="form-control" min="1" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Peso (KG/LBS)</label>
                    <input type="number" name="weights[]" class="form-control" step="0.5" min="0" required>
                </div>
            `;
            container.appendChild(setDiv);
        }
    };
})(); 