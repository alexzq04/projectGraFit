/**
 * Muestra la descripción de la tarjeta clicada sin ocultar las otras
 * @param {HTMLElement} card - La tarjeta que se ha clicado
 */
function toggleDescription(card) {
    const description = card.querySelector('.card-description');
    if (!description.classList.contains('active')) {
        description.classList.add('active');
    }
}

