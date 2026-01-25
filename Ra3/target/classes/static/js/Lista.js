/**
 * JS para la vista de gestión de usuarios (admin).
 * - Lista usuarios y departamentos
 * - Permite editar y eliminar usuarios
 */
document.addEventListener("DOMContentLoaded", () => {
    const datosMostrar = document.getElementById("datosMostrar");
    const btnSalir = document.getElementById("btnSalir");
    const volver = document.getElementById("volver");

    // Cache local de departamentos para construir los selects
    let departamentos = [];

    if (btnSalir) btnSalir.addEventListener("click", () => window.location.href = 'killSession');
    if (volver) volver.addEventListener("click", () => window.location.href = '/control');

    /**
     * Construye options HTML para el select de departamentos.
     * Selecciona el departamento actual si existe.
     */
    function optionDept(depId) {
        return departamentos.map(d => `<option value="${d.id}" ${String(d.id) === String(depId || "") ? "selected" : ""}>${d.nombre}</option>`).join("");
    }

    /**
     * Renderiza la tabla de usuarios con inputs y selects editables por fila.
     */
    function renderTablaUsuarios(lista) {
        let tablaHTML = `
      <table class="tablaDatos">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Departamento</th>
            <th>Fecha Creación</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
    `;

        lista.forEach(usuario => {
            const fecha = usuario.fechaCreacion ? new Date(usuario.fechaCreacion).toLocaleDateString() : '-';
            tablaHTML += `
        <tr data-id="${usuario.id}">
          <td>${usuario.id}</td>
          <td><input type="text" class="edit-username" value="${usuario.username}" /></td>
          <td><input type="email" class="edit-email" value="${usuario.email ?? ''}" /></td>
          <td>
            <select class="edit-rol">
              <option value="admin" ${usuario.rol === 'admin' ? 'selected' : ''}>admin</option>
              <option value="user" ${usuario.rol === 'user' ? 'selected' : ''}>user</option>
            </select>
          </td>
          <td>
            <select class="edit-departamento">
              <option value="">(sin departamento)</option>
              ${optionDept(usuario.departamentoId)}
            </select>
          </td>
          <td>${fecha}</td>
          <td>
            <select class="edit-estado">
              <option value="true" ${usuario.estado ? 'selected' : ''}>Activo</option>
              <option value="false" ${!usuario.estado ? 'selected' : ''}>Inactivo</option>
            </select>
          </td>
          <td>
            <button class="btnGuardar">Guardar</button>
            <button class="btnEliminar">Eliminar</button>
          </td>
        </tr>
      `;
        });

        tablaHTML += `</tbody></table>`;
        if (datosMostrar) datosMostrar.innerHTML = tablaHTML;

        // Guardar cambios de una fila
        document.querySelectorAll(".btnGuardar").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const tr = e.target.closest("tr");
                const id = tr.getAttribute("data-id");
                const username = tr.querySelector(".edit-username").value.trim();
                const email = tr.querySelector(".edit-email").value.trim();
                const rol = tr.querySelector(".edit-rol").value;
                const estado = tr.querySelector(".edit-estado").value === "true";
                const departamentoIdRaw = tr.querySelector(".edit-departamento").value;
                const departamentoId = departamentoIdRaw ? Number(departamentoIdRaw) : null;

                actualizarUsuario(id, { username, email, rol, estado, departamentoId });
            });
        });

        // Eliminar usuario de una fila
        document.querySelectorAll(".btnEliminar").forEach(btn => {
            btn.addEventListener("click", (e) => {
                const tr = e.target.closest("tr");
                const id = tr.getAttribute("data-id");
                const nombre = tr.querySelector(".edit-username").value.trim();

                if (!confirm(`¿Eliminar usuario "${nombre}" (ID ${id})? Esta acción no se puede deshacer.`)) return;

                eliminarUsuario(id);
            });
        });
    }

    /**
     * Carga lista de usuarios del backend (solo admin).
     */
    function cargarUsuarios() {
        fetch('/admin/verUsuarios')
            .then((response) => {
                if (response.ok) return response.json();
                if (response.status === 401) throw new Error("401: No autenticado");
                if (response.status === 403) throw new Error("403: Sin permisos");
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            })
            .then((data) => renderTablaUsuarios(data))
            .catch((error) => {
                console.error("Error al obtener usuarios:", error);
                if (datosMostrar) datosMostrar.innerHTML = `<p class="error">${error.message}</p>`;
            });
    }

    /**
     * Envía al backend los cambios de un usuario (PUT /admin/usuarios/{id})
     */
    function actualizarUsuario(id, payload) {
        fetch(`/admin/usuarios/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        })
            .then(res => {
                if (res.ok) return res.json();
                if (res.status === 400) throw new Error("Datos inválidos");
                if (res.status === 409) throw new Error("Email/Username ya en uso");
                throw new Error(`Error ${res.status}: ${res.statusText}`);
            })
            .then(() => cargarUsuarios())
            .catch(err => {
                console.error("Error al actualizar usuario:", err);
                alert(err.message || "No se pudo actualizar el usuario.");
            });
    }

    /**
     * Borra un usuario (DELETE /admin/usuarios/{id}) y recarga la lista.
     */
    function eliminarUsuario(id) {
        fetch(`/admin/usuarios/${id}`, { method: "DELETE" })
            .then(res => {
                if (res.status === 204) return;
                if (res.status === 401) throw new Error("No autenticado");
                if (res.status === 403) throw new Error("Sin permisos");
                if (res.status === 404) throw new Error("Usuario no encontrado");
                if (res.status === 409) throw new Error("No puedes eliminar tu propia cuenta mientras estás logueado");
                throw new Error(`Error ${res.status}: ${res.statusText}`);
            })
            .then(() => cargarUsuarios())
            .catch(err => {
                console.error("Error al eliminar usuario:", err);
                alert(err.message || "No se pudo eliminar el usuario.");
            });
    }

    /**
     * Carga departamentos y luego usuarios (para que el select se construya bien).
     */
    function cargarDepartamentosYUsuarios() {
        fetch('/departamentos/lista')
            .then(res => {
                if (res.ok) return res.json();
                if (res.status === 401) throw new Error("No autenticado");
                if (res.status === 403) throw new Error("Sin permisos");
                throw new Error(`Error ${res.status}`);
            })
            .then(data => { departamentos = data; cargarUsuarios(); })
            .catch(err => {
                console.error("Error al cargar departamentos:", err);
                departamentos = [];
                cargarUsuarios();
            });
    }

    // Inicial
    cargarDepartamentosYUsuarios();
});