function updateSetInputs(numberOfSets) {
    const container = document.getElementById('setConfigurations');
    const currentSets = container.children.length;
    
    // Si necesitamos más sets
    while (container.children.length < numberOfSets) {
        const setNumber = container.children.length + 1;
        const setDiv = document.createElement('div');
        setDiv.className = 'card mb-3';
        setDiv.innerHTML = `
            <div class="card-body">
                <h5 class="card-title">Serie ${setNumber}</h5>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="repetitions${setNumber-1}" class="form-label">Repeticiones</label>
                        <input type="number" 
                                id="repetitions${setNumber-1}"
                                name="setConfigurations[].repetitions"
                                class="form-control" 
                                min="1" required>
                    </div>
                    <div class="col-md-6">
                        <label for="weight${setNumber-1}" class="form-label">Peso (kg)</label>
                        <input type="number" 
                                id="weight${setNumber-1}"
                                name="setConfigurations[].weight"
                                class="form-control" 
                                step="0.5" min="0" required>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(setDiv);
    }
    
    // Si necesitamos menos sets
    while (container.children.length > numberOfSets) {
        container.removeChild(container.lastChild);
    }
} 