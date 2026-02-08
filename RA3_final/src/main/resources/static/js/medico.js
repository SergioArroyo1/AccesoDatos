const contenedorPacientes = document.getElementById("datosPacientes");
let listaPacientes = [];

// Usa ruta absoluta como en admin.js
fetch("/medico/verpacientes")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        listaPacientes = data;
        crearTablaPacientes(data);
    })
    .catch(error => {
        console.error("Error al cargar pacientes:", error);
        contenedorPacientes.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar los pacientes
            </div>
        `;
    });

/* ===== FUNCION PARA CREAR TABLA ===== */

function crearTablaPacientes(pacientes) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";

    // Cabecera
    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellidos</th>
                <th>DNI</th>
                <th>Teléfono</th>
                <th>Fecha Nacimiento</th>
                <th>Historial</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    pacientes.forEach(p => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.nombre}</td>
            <td>${p.apellidos}</td>
            <td>${p.dni}</td>
            <td>${p.telefono}</td>
            <td>${formatearFecha(p.fechaNacimiento)}</td>
            <td>${p.historial}</td>
            <td>
                <span class="badge ${p.activo ? 'bg-success' : 'bg-danger'}">
                    ${p.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editarPaciente(${p.id})">
                    Editar
                </button>
                <button class="btn btn-sm btn-danger" onclick="eliminarPaciente(${p.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedorPacientes.innerHTML = "";
    contenedorPacientes.appendChild(table);
}

/* ===== MODAL EDITAR ===== */

function editarPaciente(id) {
    const paciente = listaPacientes.find(p => p.id === id);
    if (!paciente) return;

    // Eliminar modal previo si existe
    const previo = document.getElementById("modalEditarPaciente");
    if (previo) previo.remove();

    const fechaInput = formatoFechaInput(paciente.fechaNacimiento);

    const modalHTML = `
        <div class="modal fade" id="modalEditarPaciente" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <h5 class="modal-title">Editar Paciente</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" id="editId" value="${paciente.id}">

                        <div class="mb-3">
                            <label class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="editNombre" value="${paciente.nombre}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Apellidos</label>
                            <input type="text" class="form-control" id="editApellidos" value="${paciente.apellidos}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">DNI</label>
                            <input type="text" class="form-control" id="editDni" value="${paciente.dni}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Teléfono</label>
                            <input type="text" class="form-control" id="editTelefono" value="${paciente.telefono ?? ''}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Fecha Nacimiento</label>
                            <input type="date" class="form-control" id="editFechaNacimiento" value="${fechaInput}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Historial</label>
                            <textarea class="form-control" id="editHistorial">${paciente.historial ?? ''}</textarea>
                        </div>

                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="editActivo" ${paciente.activo ? 'checked' : ''}>
                            <label class="form-check-label">Activo</label>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarEdicionPaciente()">Guardar</button>
                    </div>

                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    const modalEl = document.getElementById("modalEditarPaciente");

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

function guardarEdicionPaciente() {
    const pacienteEditado = {
        id: Number(document.getElementById("editId").value),
        nombre: document.getElementById("editNombre").value,
        apellidos: document.getElementById("editApellidos").value,
        dni: document.getElementById("editDni").value,
        telefono: document.getElementById("editTelefono").value,
        fechaNacimiento: document.getElementById("editFechaNacimiento").value,
        historial: document.getElementById("editHistorial").value,
        activo: document.getElementById("editActivo").checked
    };

    fetch(`/medico/editarpaciente`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pacienteEditado)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar");
            // Cierra el modal si sigue abierto (fallback)
            const modalEl = document.getElementById("modalEditarPaciente");
            if (modalEl) hideModal(modalEl);
            location.reload();
        })
        .catch(() => alert("Error al actualizar paciente"));
}

/* ===== ELIMINAR ===== */

function eliminarPaciente(id) {
    if (!confirm("¿Seguro que deseas eliminar este paciente?")) return;

    fetch(`/medico/eliminarpaciente/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error("Error al eliminar");
            location.reload();
        })
        .catch(() => alert("Error al eliminar paciente"));
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