const contenedorAlumnos = document.getElementById("todosAlumnos");

fetch("recepcion/veralumnos")
    .then(response => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then(data => {
        console.log(data);
        crearTablaAlumnos(data);
    })
    .catch(error => {
        console.error("Error al cargar alumnos:", error);
        contenedorAlumnos.innerHTML = `
            <div class="alert alert-danger">
                Error al cargar los Alumnos
            </div>
        `;
    });

/* ===== FUNCION PARA CREAR TABLA ===== */

function crearTablaAlumnos(alumnos) {
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
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    alumnos.forEach(p => {
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
        `;

        tbody.appendChild(tr);
    });

    contenedorAlumnos.innerHTML = "";
    contenedorAlumnos.appendChild(table);
}

/* ===== FUNCION AUXILIAR PARA FECHA ===== */

function formatearFecha(fecha) {
    if (!fecha) return "";
    return new Date(fecha).toLocaleDateString(); // solo día/mes/año
}
