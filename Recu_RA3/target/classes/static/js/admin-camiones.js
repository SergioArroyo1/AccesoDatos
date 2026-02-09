const contenedorCamiones = document.getElementById("datosCamiones");
let listaCamiones = [];

function safeFetchJson(url, init) {
    return fetch(url, init).then(async (r) => {
        if (r.ok) return r.json();
        const text = await r.text().catch(() => "");
        throw new Error(text || `HTTP ${r.status}`);
    });
}

function cargarCamiones() {
    contenedorCamiones.innerHTML = `
        <div class="text-center my-4">
            <div class="spinner-border" role="status"><span class="visually-hidden">Cargando...</span></div>
        </div>`;
    safeFetchJson("/admin/camiones/ver")
        .then(data => {
            listaCamiones = data || [];
            crearTablaCamiones(listaCamiones);
        })
        .catch(error => {
            console.error("Error al cargar camiones:", error);
            contenedorCamiones.innerHTML = `
                <div class="alert alert-danger">${error.message || "Error al cargar camiones"}</div>`;
        });
}

function crearTablaCamiones(camiones) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";
    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Matrícula</th>
                <th>Modelo</th>
                <th>Capacidad KG</th>
                <th>Estado</th>
                <th>Fecha Alta</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>`;
    const tbody = table.querySelector("tbody");

    camiones.forEach(c => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${c.id}</td>
            <td>${c.matricula}</td>
            <td>${c.modelo}</td>
            <td>${c.capacidadKg ?? 'N/A'}</td>
            <td>${c.estado}</td>
            <td>${formatearFecha(c.fechaAlta)}</td>
            <td>
                <span class="badge ${c.activo ? 'bg-success' : 'bg-danger'}">
                    ${c.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editarCamion(${c.id})">Editar</button>
                <button class="btn btn-sm btn-danger" onclick="eliminarCamion(${c.id})">Eliminar</button>
            </td>`;
        tbody.appendChild(tr);
    });

    contenedorCamiones.innerHTML = "";
    contenedorCamiones.appendChild(table);
}

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleDateString();
}

function formatoFechaInput(fecha) {
    if (!fecha) return "";
    const d = new Date(fecha);
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
}

function editarCamion(id) {
    const camion = listaCamiones.find(c => c.id === id);
    if (!camion) return;

    const previo = document.getElementById("modalEditarCamion");
    if (previo) previo.remove();

    const fechaInput = formatoFechaInput(camion.fechaAlta);

    const modalHTML = `
        <div class="modal fade" id="modalEditarCamion" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog"><div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Editar Camión</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="editId" value="${camion.id}">
                    <div class="mb-3">
                        <label class="form-label">Matrícula</label>
                        <input type="text" class="form-control" id="editMatricula" value="${camion.matricula}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Modelo</label>
                        <input type="text" class="form-control" id="editModelo" value="${camion.modelo}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Capacidad KG</label>
                        <input type="number" class="form-control" id="editCapacidadKg" value="${camion.capacidadKg ?? ''}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Estado</label>
                        <select class="form-select" id="editEstado">
                            <option value="DISPONIBLE" ${camion.estado === 'DISPONIBLE' ? 'selected' : ''}>Disponible</option>
                            <option value="EN_RUTA" ${camion.estado === 'EN_RUTA' ? 'selected' : ''}>En Ruta</option>
                            <option value="MANTENIMIENTO" ${camion.estado === 'MANTENIMIENTO' ? 'selected' : ''}>Mantenimiento</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Fecha Alta</label>
                        <input type="date" class="form-control" id="editFechaAlta" value="${fechaInput}">
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="editActivo" ${camion.activo ? 'checked' : ''}>
                        <label class="form-check-label">Activo</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button class="btn btn-primary" onclick="guardarEdicionCamion()">Guardar</button>
                </div>
            </div></div>
        </div>`;
    document.body.insertAdjacentHTML("beforeend", modalHTML);

    const modalEl = document.getElementById("modalEditarCamion");
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}

function guardarEdicionCamion() {
    const payload = {
        id: Number(document.getElementById("editId").value),
        matricula: document.getElementById("editMatricula").value,
        modelo: document.getElementById("editModelo").value,
        capacidadKg: Number(document.getElementById("editCapacidadKg").value || 0),
        estado: document.getElementById("editEstado").value,
        fechaAlta: document.getElementById("editFechaAlta").value,
        activo: document.getElementById("editActivo").checked
    };
    fetch("/admin/camiones/editar", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(async r => {
            if (r.ok) {
                const modalEl = document.getElementById("modalEditarCamion");
                if (modalEl) bootstrap.Modal.getInstance(modalEl)?.hide();
                cargarCamiones();
            } else {
                const text = await r.text().catch(() => "Error al actualizar");
                alert(text);
            }
        });
}

function eliminarCamion(id) {
    if (!confirm("¿Seguro que deseas eliminar este camión?")) return;
    fetch(`/admin/camiones/eliminar/${id}`, { method: "DELETE" })
        .then(async r => {
            if (r.ok) {
                cargarCamiones();
            } else {
                const text = await r.text().catch(() => "Error al eliminar");
                // Muestra solo la primera línea del error
                alert((text || "Error al eliminar").split('\n')[0]);
            }
        })
        .catch(() => alert("Error de red al eliminar camión"));
}
// Inicial
cargarCamiones();