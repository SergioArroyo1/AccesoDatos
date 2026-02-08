const contenedorAlumnos = document.getElementById("datosAlumnos");
let listaAlumnos = [];

// Usa ruta absoluta como en admin.js
fetch("/profesor/veralumnos")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        listaAlumnos = data;
        crearTablaAlumnos(data);
    })
    .catch(error => {
        console.error("Error al cargar los alumnos:", error);
        contenedorAlumnos.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar los alumnos
            </div>
        `;
    });

/* ===== FUNCION PARA CREAR TABLA ===== */

function crearTablaAlumnos(alumno) {
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
                <th>Curso</th>
                <th>Fecha Nacimiento</th>
                <th>Observaciones</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    alumno.forEach(p => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.nombre}</td>
            <td>${p.apellidos}</td>
            <td>${p.dni}</td>
            <td>${p.curso}</td>
            <td>${formatearFecha(p.fechaNacimiento)}</td>
            <td>${p.observaciones}</td>
            <td>
                <span class="badge ${p.activo ? 'bg-success' : 'bg-danger'}">
                    ${p.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editarAlumno(${p.id})">
                    Editar
                </button>
                <button class="btn btn-sm btn-danger" onclick="eliminarAlumno(${p.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedorAlumnos.innerHTML = "";
    contenedorAlumnos.appendChild(table);
}

/* ===== MODAL EDITAR ===== */

function editarAlumno(id) {
    const alumno = listaAlumnos.find(p => p.id === id);
    if (!alumno) return;

    // Eliminar modal previo si existe
    const previo = document.getElementById("modalEditarAlumno");
    if (previo) previo.remove();

    const fechaInput = formatoFechaInput(alumno.fechaNacimiento);

    const modalHTML = `
        <div class="modal fade" id="modalEditarAlumno" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <h5 class="modal-title">Editar Alumno</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" id="editId" value="${alumno.id}">

                        <div class="mb-3">
                            <label class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="editNombre" value="${alumno.nombre}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Apellidos</label>
                            <input type="text" class="form-control" id="editApellidos" value="${alumno.apellidos}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">DNI</label>
                            <input type="text" class="form-control" id="editDni" value="${alumno.dni}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Curso</label>
                            <input type="text" class="form-control" id="editCurso" value="${alumno.curso?? ''}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Fecha Nacimiento</label>
                            <input type="date" class="form-control" id="editFechaNacimiento" value="${fechaInput}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Observaciones</label>
                            <textarea class="form-control" id="editObservaciones">${alumno.observaciones ?? ''}</textarea>
                        </div>

                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="editActivo" ${alumno.activo ? 'checked' : ''}>
                            <label class="form-check-label">Activo</label>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarEdicionAlumno()">Guardar</button>
                    </div>

                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    const modalEl = document.getElementById("modalEditarAlumno");

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

function guardarEdicionAlumno() {
    const alumnoEditado = {
        id: Number(document.getElementById("editId").value),
        nombre: document.getElementById("editNombre").value,
        apellidos: document.getElementById("editApellidos").value,
        dni: document.getElementById("editDni").value,
        curso: document.getElementById("editCurso").value,
        fechaNacimiento: document.getElementById("editFechaNacimiento").value,
        observaciones: document.getElementById("editObservaciones").value,
        activo: document.getElementById("editActivo").checked
    };

    fetch(`/profesor/editaralumno`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(alumnoEditado)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar");
            // Cierra el modal si sigue abierto (fallback)
            const modalEl = document.getElementById("modalEditarAlumno");
            if (modalEl) hideModal(modalEl);
            location.reload();
        })
        .catch(() => alert("Error al actualizar el alumno"));
}

/* ===== ELIMINAR ===== */

function eliminarAlumno(id) {
    if (!confirm("¿Seguro que deseas eliminar este alumno?")) return;

    fetch(`/profesor/eliminaralumno/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error("Error al eliminar");
            location.reload();
        })
        .catch(() => alert("Error al eliminar el alumno"));
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