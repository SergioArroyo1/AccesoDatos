const contenedorCamiones = document.getElementById("datosCamiones");
let listaCamiones = [];

// Usa ruta absoluta como en admin.js
fetch("/coordinador/vercamiones")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        listaCamiones = data;
        crearTablaCamiones(data);
    })
    .catch(error => {
        console.error("Error al cargar camiones:", error);
        contenedorCamiones.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar los camiones
            </div>
        `;
    });

/* ===== FUNCION PARA CREAR TABLA ===== */

function crearTablaCamiones(camiones) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";

    // Cabecera
    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Matricula</th>
                <th>Modelo</th>
                <th>Capacidad KG</th>
                <th>Estado</th>
                <th>Fecha Alta</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    camiones.forEach(c => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${c.id}</td>
            <td>${c.matricula}</td>
            <td>${c.modelo}</td>
            <td>${c.capacidadKG}</td>
            <td>${c.estado}</td>
            <td>${formatearFecha(c.fechaAlta)}</td>
            <td>
                <span class="badge ${c.activo ? 'bg-success' : 'bg-danger'}">
                    ${c.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editarCamion(${c.id})">
                    Editar
                </button>
                <button class="btn btn-sm btn-danger" onclick="eliminarCamion(${c.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedorCamiones.innerHTML = "";
    contenedorCamiones.appendChild(table);
}

/* ===== MODAL EDITAR ===== */

function editarCamion(id) {
    const camion = listaCamiones.find(c => c.id === id); // ← CORREGIDO de 'p' a 'c'
    if (!camion) return;

    // Eliminar modal previo si existe
    const previo = document.getElementById("modalEditarCamion");
    if (previo) previo.remove();

    const fechaInput = formatoFechaInput(camion.fechaAlta);

    const modalHTML = `
        <div class="modal fade" id="modalEditarCamion" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <h5 class="modal-title">Editar Camion</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" id="editId" value="${camion.id}">

                        <div class="mb-3">
                            <label class="form-label">Matricula</label>
                            <input type="text" class="form-control" id="editMatricula" value="${camion.matricula}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Modelo</label>
                            <input type="text" class="form-control" id="editModelo" value="${camion.modelo}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Capacidad KG</label>
                            <input type="number" class="form-control" id="editCapacidadKG" value="${camion.capacidadKG}">
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

                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    const modalEl = document.getElementById("modalEditarCamion");

    // Mostrar el modal (con Bootstrap si existe, con fallback si no)
    if (window.bootstrap && typeof bootstrap.Modal === "function") {
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    } else {
        // Fallback sin Bootstrap: mostrar el contenedor como modal simple
        modalEl.style.display = "block";
        modalEl.classList.add("show");
        modalEl.setAttribute("aria-modal", "true");
        modalEl.removeAttribute("aria-hidden");

        // Cerrar en botones que tienen data-bs-dismiss
        modalEl.querySelectorAll('[data-bs-dismiss="modal"]').forEach(btn => {
            btn.addEventListener("click", () => hideModal(modalEl));
        });
    }
}

function hideModal(modalEl) {
    modalEl.classList.remove("show");
    modalEl.style.display = "none";
    modalEl.removeAttribute("aria-modal");
    modalEl.setAttribute("aria-hidden", "true");
    // Limpia del DOM para evitar duplicados
    setTimeout(() => modalEl.remove(), 0);
}

/* ===== GUARDAR EDICION ===== */

function guardarEdicionCamion() {
    const camionEditado = {
        id: Number(document.getElementById("editId").value),
        matricula: document.getElementById("editMatricula").value,
        modelo: document.getElementById("editModelo").value,
        capacidadKG: Number(document.getElementById("editCapacidadKG").value),
        estado: document.getElementById("editEstado").value,
        fechaAlta: document.getElementById("editFechaAlta").value,
        activo: document.getElementById("editActivo").checked
    };

    fetch(`/coordinador/editarcamion`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(camionEditado)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar");
            // Cierra el modal si sigue abierto (fallback)
            const modalEl = document.getElementById("modalEditarCamion");
            if (modalEl) hideModal(modalEl);
            location.reload();
        })
        .catch(() => alert("Error al actualizar camion"));
}

/* ===== ELIMINAR ===== */

function eliminarCamion(id) {
    if (!confirm("¿Seguro que deseas eliminar este camion?")) return;

    fetch(`/coordinador/eliminarcamion/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error("Error al eliminar");
            location.reload();
        })
        .catch(() => alert("Error al eliminar camion"));
}

/* ===== FUNCION AUXILIAR PARA FECHA ===== */

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleDateString(); // solo día/mes/año
}

function formatoFechaInput(fecha) {
    if (!fecha) return "";
    const d = new Date(fecha);
    // Ajuste a formato YYYY-MM-DD para input type="date"
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
}