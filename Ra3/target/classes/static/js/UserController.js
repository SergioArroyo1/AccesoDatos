/**
 * JS de la página de usuario:
 * - Muestra datos del usuario (incluye departamento)
 * - Permite editar username/email propios
 * - Tras guardar, recarga para mantener departamento visible
 */
document.addEventListener("DOMContentLoaded", () => {
    const saludoUsuario = document.getElementById("saludoUsuario");
    const datosMostrar = document.getElementById("datosMostrar");
    const btnSalir = document.getElementById("btnSalir");

    // Botón salir -> cerrar sesión
    if (btnSalir) btnSalir.addEventListener("click", () => { window.location.href = "killSession"; });

    let userCache = null;   // cache local del usuario
    let editMode = false;   // estado de edición (true/false)

    /** Renderiza la tabla con datos y botones de edición/guardar/cancelar */
    function render(user) {
        const fecha = user.fechaCreacion ? new Date(user.fechaCreacion).toLocaleDateString() : "-";
        const estado = user.estado ? "Activo" : "Inactivo";

        const usernameField = editMode ? `<input type="text" id="editUsername" value="${user.username ?? ""}" />` : `${user.username ?? "-"}`;
        const emailField = editMode ? `<input type="email" id="editEmail" value="${user.email ?? ""}" />` : `${user.email ?? "-"}`;
        const deptoField = `${user.departamentoNombre ?? "-"}`;

        const acciones = editMode
            ? `<button id="btnGuardar">Guardar</button> <button id="btnCancelar">Cancelar</button>`
            : `<button id="btnEditar">Editar</button>`;

        datosMostrar.innerHTML = `
      <table class="tablaDatos">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Departamento</th>
            <th>Rol</th>
            <th>Estado</th>
            <th>Fecha Creación</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>${user.id}</td>
            <td>${usernameField}</td>
            <td>${emailField}</td>
            <td>${deptoField}</td>
            <td>${user.rol ?? "-"}</td>
            <td>${estado}</td>
            <td>${fecha}</td>
            <td>${acciones}</td>
          </tr>
        </tbody>
      </table>
    `;

        if (saludoUsuario) saludoUsuario.textContent = `Bienvenido de nuevo ${user.username}`;

        const btnEditar = document.getElementById("btnEditar");
        const btnGuardar = document.getElementById("btnGuardar");
        const btnCancelar = document.getElementById("btnCancelar");

        // Entra en modo edición
        if (btnEditar) btnEditar.addEventListener("click", () => { editMode = true; render(userCache); });
        // Cancela edición (vuelve a mostrar datos originales)
        if (btnCancelar) btnCancelar.addEventListener("click", () => { editMode = false; render(userCache); });

        // Guarda cambios (username/email) y recarga datos completos
        if (btnGuardar) {
            btnGuardar.addEventListener("click", () => {
                const username = document.getElementById("editUsername").value.trim();
                const email = document.getElementById("editEmail").value.trim();

                fetch("/user/datos", {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, email })
                })
                    .then(res => {
                        if (res.ok) return res.json();
                        if (res.status === 400) throw new Error("Datos inválidos (revisa username/email)");
                        if (res.status === 409) throw new Error("Username o email ya en uso");
                        if (res.status === 401) throw new Error("Sesión expirada");
                        throw new Error(`Error ${res.status}: ${res.statusText}`);
                    })
                    .then(() => {
                        // Re-carga desde el GET que siempre incluye departamento
                        return fetch("/user/datos").then(r => r.json());
                    })
                    .then((fresh) => {
                        userCache = fresh;
                        editMode = false;
                        render(userCache);
                        alert("Datos actualizados.");
                    })
                    .catch(err => {
                        console.error("No se pudo actualizar:", err);
                        alert(err.message || "No se pudo actualizar los datos.");
                    });
            });
        }
    }

    // Carga inicial de los datos del usuario
    fetch("/user/datos")
        .then((response) => {
            if (response.ok) return response.json();
            if (response.status === 401) throw new Error("Sesión no válida");
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        })
        .then((user) => { userCache = user; render(userCache); })
        .catch((error) => {
            console.error("Fallo en datos de usuario:", error);
            if (datosMostrar) {
                datosMostrar.innerHTML = `<p class="error">${error.message}</p><button id="btnSalirInline">Salir</button>`;
                const btnSalirInline = document.getElementById("btnSalirInline");
                if (btnSalirInline) btnSalirInline.addEventListener("click", () => window.location.href = "killSession");
            }
        });
});