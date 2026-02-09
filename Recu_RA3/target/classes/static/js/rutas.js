const contenedorRutas = document.getElementById("datosRutas");
let listaRutas = [];

// Cargar rutas al iniciar
fetch("/rutas/ver")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        listaRutas = data;
        crearTablaRutas(data);
    })
    .catch(error => {
        console.error("Error al cargar rutas:", error);
        contenedorRutas.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar las rutas
            </div>
        `;
    });

/* ===== CREAR TABLA ===== */

function crearTablaRutas(rutas) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";

    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Zona</th>
                <th>Día Semana</th>
                <th>Hora Inicio</th>
                <th>Hora Fin</th>
                <th>Activa</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    rutas.forEach(ruta => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${ruta.id}</td>
            <td>${ruta.nombre}</td>
            <td>${ruta.zona}</td>
            <td>${ruta.diaSemana}</td>
            <td>${ruta.horaInicio}</td>
            <td>${ruta.horaFin}</td>
            <td>
                <span class="badge ${ruta.activa ? 'bg-success' : 'bg-danger'}">
                    ${ruta.activa ? 'Activa' : 'Inactiva'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editarRuta(${ruta.id})">
                    Editar
                </button>
                <button class="btn btn-sm btn-danger" onclick="eliminarRuta(${ruta.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedorRutas.innerHTML = "";
    contenedorRutas.appendChild(table);
}

/* ===== MODAL CREAR RUTA ===== */

function mostrarModalCrearRuta() {
    document.getElementById("modalCrearRuta")?.remove();

    const modalHTML = `
        <div class="modal fade" id="modalCrearRuta" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Nueva Ruta</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="crearNombre">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Zona</label>
                            <input type="text" class="form-control" id="crearZona">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Día Semana</label>
                            <select class="form-select" id="crearDiaSemana">
                                <option value="LUNES">Lunes</option>
                                <option value="MARTES">Martes</option>
                                <option value="MIERCOLES">Miércoles</option>
                                <option value="JUEVES">Jueves</option>
                                <option value="VIERNES">Viernes</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hora Inicio</label>
                            <input type="time" class="form-control" id="crearHoraInicio">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hora Fin</label>
                            <input type="time" class="form-control" id="crearHoraFin">
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="crearActiva" checked>
                            <label class="form-check-label">Activa</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarNuevaRuta()">Crear</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);
    const modal = new bootstrap.Modal(document.getElementById("modalCrearRuta"));
    modal.show();
}

/* ===== GUARDAR NUEVA RUTA ===== */

function guardarNuevaRuta() {
    const nuevaRuta = {
        nombre: document.getElementById("crearNombre").value,
        zona: document.getElementById("crearZona").value,
        diaSemana: document.getElementById("crearDiaSemana").value,
        horaInicio: document.getElementById("crearHoraInicio").value,
        horaFin: document.getElementById("crearHoraFin").value,
        activa: document.getElementById("crearActiva").checked
    };

    fetch("/rutas/crear", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(nuevaRuta)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al crear ruta");
            location.reload();
        })
        .catch(error => alert("Error al crear ruta: " + error.message));
}

/* ===== MODAL EDITAR RUTA ===== */

function editarRuta(id) {
    const ruta = listaRutas.find(r => r.id === id);
    if (!ruta) return;

    document.getElementById("modalEditarRuta")?.remove();

    const modalHTML = `
        <div class="modal fade" id="modalEditarRuta" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Editar Ruta</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="editId" value="${ruta.id}">
                        
                        <div class="mb-3">
                            <label class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="editNombre" value="${ruta.nombre}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Zona</label>
                            <input type="text" class="form-control" id="editZona" value="${ruta.zona}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Día Semana</label>
                            <select class="form-select" id="editDiaSemana">
                                <option value="LUNES" ${ruta.diaSemana === 'LUNES' ? 'selected' : ''}>Lunes</option>
                                <option value="MARTES" ${ruta.diaSemana === 'MARTES' ? 'selected' : ''}>Martes</option>
                                <option value="MIERCOLES" ${ruta.diaSemana === 'MIERCOLES' ? 'selected' : ''}>Miércoles</option>
                                <option value="JUEVES" ${ruta.diaSemana === 'JUEVES' ? 'selected' : ''}>Jueves</option>
                                <option value="VIERNES" ${ruta.diaSemana === 'VIERNES' ? 'selected' : ''}>Viernes</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hora Inicio</label>
                            <input type="time" class="form-control" id="editHoraInicio" value="${ruta.horaInicio}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hora Fin</label>
                            <input type="time" class="form-control" id="editHoraFin" value="${ruta.horaFin}">
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="editActiva" ${ruta.activa ? 'checked' : ''}>
                            <label class="form-check-label">Activa</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarEdicionRuta()">Guardar</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);
    const modal = new bootstrap.Modal(document.getElementById("modalEditarRuta"));
    modal.show();
}

/* ===== GUARDAR EDICION RUTA ===== */

function guardarEdicionRuta() {
    const rutaEditada = {
        id: Number(document.getElementById("editId").value),
        nombre: document.getElementById("editNombre").value,
        zona: document.getElementById("editZona").value,
        diaSemana: document.getElementById("editDiaSemana").value,
        horaInicio: document.getElementById("editHoraInicio").value,
        horaFin: document.getElementById("editHoraFin").value,
        activa: document.getElementById("editActiva").checked
    };

    fetch("/rutas/editar", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(rutaEditada)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar");
            location.reload();
        })
        .catch(error => alert("Error al actualizar ruta: " + error.message));
}

/* ===== ELIMINAR RUTA ===== */

function eliminarRuta(id) {
    if (!confirm("¿Seguro que deseas eliminar esta ruta?")) return;

    fetch(`/rutas/eliminar/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error("Error al eliminar");
            location.reload();
        })
        .catch(error => alert("Error al eliminar ruta: " + error.message));
}