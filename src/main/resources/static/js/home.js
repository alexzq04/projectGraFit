document.addEventListener("DOMContentLoaded", () => {
    const fechaInput = document.getElementById("fecha");
    if (fechaInput) {
        const hoy = new Date();
        const año = hoy.getFullYear();
        const mes = String(hoy.getMonth() + 1).padStart(2, '0');
        const dia = String(hoy.getDate()).padStart(2, '0');
        fechaInput.value = `${año}-${mes}-${dia}`;
    }
});

// Inicializar todos los tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

const timers = {};
let csrfToken = '';
let csrfHeader = '';

// Función para inicializar los tokens CSRF
function initializeCsrf(token, header) {
    csrfToken = token;
    csrfHeader = header;
}

function startTimer(trainingId, setNumber) {
    if (!timers[trainingId]) {
        timers[trainingId] = {
            seconds: 0,
            interval: null
        };
    }

    if (!timers[trainingId].interval) {
        timers[trainingId].interval = setInterval(() => {
            timers[trainingId].seconds++;
            updateTimerDisplay(trainingId, setNumber);
        }, 1000);
    }
}

function pauseTimer(trainingId, setNumber) {
    if (timers[trainingId] && timers[trainingId].interval) {
        clearInterval(timers[trainingId].interval);
        timers[trainingId].interval = null;
    }
}

function resetTimer(trainingId, setNumber) {
    if (timers[trainingId]) {
        clearInterval(timers[trainingId].interval);
        timers[trainingId].interval = null;
        timers[trainingId].seconds = 0;
        updateTimerDisplay(trainingId, setNumber);
    }
}

function updateTimerDisplay(trainingId, setNumber) {
    const minutes = Math.floor(timers[trainingId].seconds / 60);
    const seconds = timers[trainingId].seconds % 60;
    const display = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    document.getElementById(`timer-${trainingId}-${setNumber}`).textContent = display;
    document.getElementById(`timerValue-${trainingId}-${setNumber}`).value = display;
}

const editModal = new bootstrap.Modal(document.getElementById('editSetModal'));

function openEditModal(button) {
    const historyId = button.getAttribute('data-history-id');
    const setId = button.getAttribute('data-set-id');
    const reps = button.getAttribute('data-reps');
    const weight = button.getAttribute('data-weight');
    const timer = button.getAttribute('data-timer');
    const note = button.getAttribute('data-note');

    if (!historyId || !setId || isNaN(historyId) || isNaN(setId)) {
        console.error('Error: historyId o setId es null o inválido');
        return;
    }

    document.getElementById('editHistoryId').value = historyId;
    document.getElementById('editSetId').value = setId;
    document.getElementById('editReps').value = reps;
    document.getElementById('editWeight').value = weight;
    document.getElementById('editTimer').value = timer || '00:00';
    document.getElementById('editNote').value = note || '';

    editModal.show();
}

function deleteSet(historyId, setId) {
    if (confirm('¿Estás seguro de que quieres eliminar este set?')) {
        fetch(`/exercise-history/delete/${historyId}/${setId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        }).then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                alert('Error al eliminar el set');
            }
        });
    }
}

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function () {
    // Manejar el input del timer
    const editTimer = document.getElementById('editTimer');
    if (editTimer) {
        editTimer.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 4) value = value.slice(0, 4);
            if (value.length >= 2) {
                value = value.slice(0, 2) + ':' + value.slice(2);
            }
            e.target.value = value;
        });
    }

    // Manejar el formulario de edición
    const editForm = document.getElementById('editSetForm');
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            
            fetch('/exercise-history/edit', {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                },
                body: formData
            }).then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    alert('Error al actualizar el set');
                }
            });
        });
    }

    // Manejar los formularios de agregar ejercicio
    const forms = document.querySelectorAll('form[action="/exercise-history/add"]');
    forms.forEach(form => {
        form.addEventListener('submit', function () {
            const trainingId = this.querySelector('input[name="trainingId"]').value;
            const setNumber = this.querySelector('input[name="setNumber"]').value;
            const timerValue = document.getElementById(`timer-${trainingId}-${setNumber}`).textContent;
            document.getElementById(`timerValue-${trainingId}-${setNumber}`).value = timerValue;
        });
    });
});
