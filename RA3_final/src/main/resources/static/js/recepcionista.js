const contenedorPacientes = document.getElementById("todosPacientes");

fetch("recepcionista/verpacientes")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        console.log(data);
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
        `;

        tbody.appendChild(tr);
    });

    contenedorPacientes.innerHTML = "";
    contenedorPacientes.appendChild(table);
}

/* ===== FUNCION AUXILIAR PARA FECHA ===== */

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleDateString(); // solo día/mes/año
}
