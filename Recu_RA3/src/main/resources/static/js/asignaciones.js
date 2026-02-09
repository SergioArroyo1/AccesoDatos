const contenedorAsignaciones = document.getElementById("datosAsignaciones");
let listaAsignaciones = [];
let listaCamiones = [];
let listaRutas = [];

// Cargar asignaciones al iniciar
Promise.all([
    fetch("/asignaciones/ver").then(r => r.json()),
    fetch("/coordinador/vercamiones").then(r => r.json()),
    fetch("/rutas/ver").then(r => r.json())
])
    .then(([asignaciones, camiones, rutas]) => {
        listaAsignaciones = asignaciones;
        listaCamiones = camiones;
        listaRutas = rutas;
        crearTablaAsignaciones(asignaciones);
    })
    .catch(error => {
        console.error("Error al cargar datos:", error);
        contenedorAsignaciones.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar las asignaciones
            </div>
        `;
    });

/* ===== CREAR TABLA ===== */

function crearTablaAsignaciones(asignaciones) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";

    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Camión</th>
                <th>Matrícula</th>
                <th>Ruta</th>
                <th>Zona</th>
                <th>Día</th>
                <th>Fecha Asignación</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    asignaciones.forEach(asig => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${asig.id}</td>
            <td>${asig.camion?.modelo || 'N/A'}</td>
            <td>${asig.camion?.matricula || 'N/A'}</td>
            <td>${asig.ruta?.nombre || 'N/A'}</td>
            <td>${asig.ruta?.zona || 'N/A'}</td>
            <td>${asig.ruta?.diaSemana || 'N/A'}</td>
            <td>${formatearFecha(asig.fechaAsignacion)}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="eliminarAsignacion(${asig.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedorAsignaciones.innerHTML = "";
    contenedorAsignaciones.appendChild(table);
}

/* ===== MODAL CREAR ASIGNACION ===== */

function mostrarModalCrearAsignacion() {
    document.getElementById("modalCrearAsignacion")?.remove();

    // Generar opciones de camiones
    const opcionesCamiones = listaCamiones.map(c =>
        `<option value="${c.id}">${c.matricula} - ${c.modelo}</option>`
    ).join('');

    // Generar opciones de rutas
    const opcionesRutas = listaRutas.map(r =>
        `<option value="${r.id}">${r.nombre} - ${r.zona} (${r.diaSemana})</option>`
    ).join('');

    const modalHTML = `
        <div class="modal fade" id="modalCrearAsignacion" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Nueva Asignación</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Seleccionar Camión</label>
                            <select class="form-select" id="crearCamionId">
                                <option value="">-- Selecciona un camión --</option>
                                ${opcionesCamiones}
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Seleccionar Ruta</label>
                            <select class="form-select" id="crearRutaId">
                                <option value="">-- Selecciona una ruta --</option>
                                ${opcionesRutas}
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarNuevaAsignacion()">Crear</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);
    const modal = new bootstrap.Modal(document.getElementById("modalCrearAsignacion"));
    modal.show();
}

/* ===== GUARDAR NUEVA ASIGNACION ===== */

function guardarNuevaAsignacion() {
    const camionId = document.getElementById("crearCamionId").value;
    const rutaId = document.getElementById("crearRutaId").value;

    if (!camionId || !rutaId) {
        alert("Debes seleccionar un camión y una ruta");
        return;
    }

    const nuevaAsignacion = {
        camionId: Number(camionId),
        rutaId: Number(rutaId)
    };

    fetch("/asignaciones/crear", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(nuevaAsignacion)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al crear asignación");
            location.reload();
        })
        .catch(error => alert("Error al crear asignación: " + error.message));
}

/* ===== ELIMINAR ASIGNACION ===== */

function eliminarAsignacion(id) {
    if (!confirm("¿Seguro que deseas eliminar esta asignación?")) return;

    fetch(`/asignaciones/eliminar/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error("Error al eliminar");
            location.reload();
        })
        .catch(error => alert("Error al eliminar asignación: " + error.message));
}

/* ===== UTILS ===== */

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleDateString();
}