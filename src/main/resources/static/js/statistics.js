let exercises = [];
let charts = []; // Array para almacenar las instancias de las gráficas

// Función para inicializar los ejercicios
function initializeExercises(exercisesData) {
    exercises = exercisesData;
    // Cargar las gráficas después de inicializar los ejercicios
    exercises.forEach((exercise, index) => {
        loadExerciseChart(exercise, index);
    });
}

// Función para obtener la configuración de la gráfica según el tipo
function getChartConfig(data, type) {
    const baseConfig = {
        data: {
            labels: data.progress.dates,
            datasets: [
                {
                    label: 'Peso (kg)',
                    data: data.progress.weights,
                    borderColor: 'rgb(220, 53, 69)',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    yAxisID: 'y',
                },
                {
                    label: 'Repeticiones',
                    data: data.progress.repetitions,
                    borderColor: 'rgb(13, 110, 253)',
                    backgroundColor: 'rgba(13, 110, 253, 0.1)',
                    yAxisID: 'y1',
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            scales: {
                x: {
                    display: true,
                    title: {
                        display: true,
                        text: 'Fecha'
                    }
                },
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Peso (kg)'
                    },
                    min: 0,
                    suggestedMax: Math.max(...data.progress.weights) + 10
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Repeticiones'
                    },
                    min: 0,
                    suggestedMax: Math.max(...data.progress.repetitions) + 5,
                    grid: {
                        drawOnChartArea: false
                    }
                }
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        boxWidth: 12,
                        padding: 15
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 10,
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'white',
                    borderWidth: 1,
                    displayColors: true,
                    callbacks: {
                        title: function(context) {
                            const index = context[0].dataIndex;
                            const date = data.progress.dates[index];
                            const setNumber = data.progress.setNumbers[index];
                            return `${date} - Serie ${setNumber}`;
                        },
                        label: function(context) {
                            if (context.dataset.label === 'Peso (kg)') {
                                return `Peso: ${context.parsed.y} kg`;
                            } else {
                                return `Repeticiones: ${context.parsed.y}`;
                            }
                        },
                        afterBody: function(context) {
                            const index = context[0].dataIndex;
                            const note = data.progress.notes[index];
                            return note ? ['Nota: ' + note] : [];
                        }
                    }
                }
            }
        }
    };

    // Configuraciones específicas según el tipo de gráfica
    switch(type) {
        case 'line':
            baseConfig.type = 'line';
            baseConfig.data.datasets.forEach(dataset => {
                dataset.tension = 0.4;
                dataset.fill = true;
                dataset.pointRadius = 4;
                dataset.pointHoverRadius = 6;
            });
            break;
        case 'bar':
            baseConfig.type = 'bar';
            baseConfig.data.datasets.forEach(dataset => {
                dataset.backgroundColor = dataset.borderColor;
            });
            break;
    }

    return baseConfig;
}

// Función para actualizar todas las gráficas
function updateAllCharts() {
    const selectedType = document.getElementById('chartType').value;
    exercises.forEach((exercise, index) => {
        if (charts[index]) {
            fetch(`/statistics/exercise/${exercise.idExercise}/progress`)
                .then(response => response.json())
                .then(data => {
                    const newConfig = getChartConfig(data, selectedType);
                    charts[index].destroy();
                    const ctx = document.getElementById(`chart${index}`).getContext('2d');
                    charts[index] = new Chart(ctx, newConfig);
                });
        }
    });
}

// Función para cargar y mostrar la gráfica de un ejercicio
function loadExerciseChart(exercise, index) {
    fetch(`/statistics/exercise/${exercise.idExercise}/progress`)
        .then(response => response.json())
        .then(data => {
            if (!data.progress || !data.progress.dates || data.progress.dates.length === 0) {
                throw new Error('No hay datos de progreso disponibles');
            }
            // Crear contenedor para la gráfica
            const chartDiv = document.createElement('div');
            chartDiv.className = 'col-md-6 mb-4';
            chartDiv.innerHTML = `
                <div class="card shadow-sm chart-card">
                    <div class="card-header bg-danger text-white">
                        <h5 class="mb-0">${exercise.name}</h5>
                    </div>
                    <div class="card-body">
                        <div class="chart-container">
                            <canvas id="chart${index}"></canvas>
                        </div>
                    </div>
                </div>
            `;
            document.getElementById('chartsContainer').appendChild(chartDiv);

            // Crear la gráfica con el tipo seleccionado
            const selectedType = document.getElementById('chartType').value;
            const config = getChartConfig(data, selectedType);
            const ctx = document.getElementById(`chart${index}`).getContext('2d');
            charts[index] = new Chart(ctx, config);
        })
        .catch(() => {
            // Mostrar mensaje de error en la interfaz
            const errorDiv = document.createElement('div');
            errorDiv.className = 'col-md-6 mb-4';
            errorDiv.innerHTML = `
                <div class="card shadow-sm">
                    <div class="card-header bg-danger text-white">
                        <h5 class="mb-0">${exercise.name}</h5>
                    </div>
                    <div class="card-body">
                        <p class="text-danger">No hay datos de progreso para mostrar en los últimos 30 días.</p>
                    </div>
                </div>
            `;
            document.getElementById('chartsContainer').appendChild(errorDiv);
        });
}

// Cargar todas las gráficas al iniciar
document.addEventListener('DOMContentLoaded', function() {
    exercises.forEach((exercise, index) => {
        loadExerciseChart(exercise, index);
    });
}); 