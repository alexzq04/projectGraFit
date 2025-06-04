function toggleNote(element) {
    const noteElement = element.querySelector('.set-note');
    if (noteElement) {
        noteElement.classList.toggle('show');
    }
}

function toggleRoutine(header) {
    header.classList.toggle('collapsed');
    const content = header.nextElementSibling;
    content.classList.toggle('collapsed');
}

// Colapsar todas las rutinas excepto la primera al cargar
document.addEventListener('DOMContentLoaded', function() {
    const routineHeaders = document.querySelectorAll('.routine-header');
    routineHeaders.forEach((header, index) => {
        if (index > 0) {
            toggleRoutine(header);
        }
    });
}); 