const contenedor = document.getElementById("datosUsuarios");
let listaUsuarios = []; // ← guardamos usuarios en memoria

fetch('/admin/verusuarios')
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        listaUsuarios = data;
        crearTablaUsuarios(data);
    })
    .catch(error => {
        console.error("Error al cargar usuarios:", error);
        contenedor.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar los usuarios
            </div>
        `;
    });

/* ===== TABLA ===== */

function crearTablaUsuarios(data) {
    const table = document.createElement("table");
    table.className = "table table-striped table-hover";

    table.innerHTML = `
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Fecha creación</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    data.forEach(usuario => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${usuario.id}</td>
            <td>${usuario.username}</td>
            <td>${usuario.email}</td>
            <td>${usuario.nombre}</td>
            <td>${usuario.rol}</td>
            <td>${formatearFecha(usuario.fechaCreacion)}</td>
            <td>
                <span class="badge ${usuario.activo ? 'bg-success' : 'bg-danger'}">
                    ${usuario.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-warning me-1"
                    onclick="editarUsuario(${usuario.id})">
                    Editar
                </button>
                <button class="btn btn-sm btn-danger"
                    onclick="eliminarUsuario(${usuario.id})">
                    Eliminar
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    contenedor.innerHTML = "";
    contenedor.appendChild(table);
}

/* ===== MODAL EDITAR ===== */

function editarUsuario(id) {
    const usuario = listaUsuarios.find(u => u.id === id);
    if (!usuario) return;

    // Eliminar modal previo si existe
    document.getElementById("modalEditarUsuario")?.remove();

    const modalHTML = `
        <div class="modal fade" id="modalEditarUsuario" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">

                    <div class="modal-header">
                        <h5 class="modal-title">Editar Usuario</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>

                    <div class="modal-body">
                        <input type="hidden" id="editId" value="${usuario.id}">

                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" class="form-control" id="editUsername" value="${usuario.username}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="editEmail" value="${usuario.email}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Nombre</label>
                            <input type="text" class="form-control" id="editNombre" value="${usuario.nombre}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Rol</label>
                            <select class="form-select" id="editRol">
                                <option value="admin" ${usuario.rol === 'admin' ? 'selected' : ''}>ADMIN</option>
                                <option value="medico" ${usuario.rol === 'medico' ? 'selected' : ''}>Medico</option>
                                <option value="recepcion" ${usuario.rol === 'recepcion' ? 'selected' : ''}>Recepcion</option>
                            </select>
                        </div>

                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="editActivo" ${usuario.activo ? 'checked' : ''}>
                            <label class="form-check-label">Activo</label>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-primary" onclick="guardarEdicion()">Guardar</button>
                    </div>

                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    const modal = new bootstrap.Modal(document.getElementById("modalEditarUsuario"));
    modal.show();
}

/* ===== GUARDAR ===== */

function guardarEdicion() {
    const usuarioEditado = {
        id: document.getElementById("editId").value,
        username: document.getElementById("editUsername").value,
        email: document.getElementById("editEmail").value,
        nombre: document.getElementById("editNombre").value,
        rol: document.getElementById("editRol").value,
        activo: document.getElementById("editActivo").checked
    };

    fetch(`/admin/editarusuario`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(usuarioEditado)
    })
        .then(response => {
            if (!response.ok) throw new Error("Error al actualizar");
            location.reload();
        })
        .catch(error => alert("Error al actualizar usuario"));
}

/* ===== ELIMINAR ===== */

function eliminarUsuario(id) {
    if (!confirm("¿Seguro que deseas eliminar este usuario?")) return;

    fetch(`/admin/eliminarusuario/${id}`, { method: 'DELETE' })
        .then(() => location.reload());
}

/* ===== UTILS ===== */

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleString();
}
