/**
 * JS para la gestión de departamentos (solo admin).
 * - Listar/Crear/Editar/Eliminar departamentos
 */
document.addEventListener("DOMContentLoaded", () => {
    const cont = document.getElementById("contenedorDepartamentos");
    const nuevoNombre = document.getElementById("nuevoNombre");
    const btnCrear = document.getElementById("btnCrear");
    const volver = document.getElementById("volver");

    // Volver al panel
    if (volver) volver.addEventListener("click", () => window.location.href = "/control");

    /** Renderiza la tabla editable de departamentos */
    function renderTabla(deps) {
        let html = `
      <table class="tablaDatos">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
    `;
        deps.forEach(d => {
            html += `
        <tr data-id="${d.id}">
          <td>${d.id}</td>
          <td><input type="text" class="edit-nombre" value="${d.nombre}" /></td>
          <td>
            <button class="btnGuardar">Guardar</button>
            <button class="btnEliminar">Eliminar</button>
          </td>
        </tr>
      `;
        });
        html += `</tbody></table>`;
        cont.innerHTML = html;

        // Guardar cambios en un departamento
        document.querySelectorAll(".btnGuardar").forEach(b => {
            b.addEventListener("click", (e) => {
                const tr = e.target.closest("tr");
                const id = tr.getAttribute("data-id");
                const nombre = tr.querySelector(".edit-nombre").value.trim();
                fetch(`/departamentos/${id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ id, nombre })
                })
                    .then(res => {
                        if (res.ok) return res.json();
                        if (res.status === 409) throw new Error("Nombre ya existe");
                        if (res.status === 400) throw new Error("Nombre inválido");
                        throw new Error(`Error ${res.status}`);
                    })
                    .then(() => cargar())
                    .catch(err => alert(err.message));
            });
        });

        // Eliminar un departamento (si no está en uso)
        document.querySelectorAll(".btnEliminar").forEach(b => {
            b.addEventListener("click", (e) => {
                const tr = e.target.closest("tr");
                const id = tr.getAttribute("data-id");
                if (!confirm("¿Eliminar departamento?")) return;
                fetch(`/departamentos/${id}`, { method: "DELETE" })
                    .then(res => {
                        if (res.status === 204) return;
                        if (res.status === 409) throw new Error("No se puede eliminar: en uso por usuarios");
                        throw new Error(`Error ${res.status}`);
                    })
                    .then(() => cargar())
                    .catch(err => alert(err.message));
            });
        });
    }

    /** Carga lista de departamentos desde el backend */
    function cargar() {
        fetch("/departamentos/lista")
            .then(res => {
                if (res.ok) return res.json();
                if (res.status === 401) throw new Error("No autenticado");
                if (res.status === 403) throw new Error("Sin permisos");
                throw new Error(`Error ${res.status}`);
            })
            .then(renderTabla)
            .catch(err => cont.innerHTML = `<p class="error">${err.message}</p>`);
    }

    // Crear nuevo departamento
    if (btnCrear) {
        btnCrear.addEventListener("click", () => {
            const nombre = (nuevoNombre.value || "").trim();
            if (!nombre) {
                alert("Introduce un nombre");
                return;
            }
            fetch("/departamentos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ nombre })
            })
                .then(res => {
                    if (res.status === 201) return res.json();
                    if (res.status === 409) throw new Error("Nombre ya existe");
                    if (res.status === 400) throw new Error("Nombre inválido");
                    throw new Error(`Error ${res.status}`);
                })
                .then(() => { nuevoNombre.value = ""; cargar(); })
                .catch(err => alert(err.message));
        });
    }

    // Inicial
    cargar();
});