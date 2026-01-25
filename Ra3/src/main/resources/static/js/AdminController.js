/**
 * JS para la página del panel administrador (resumen del admin logueado).
 * - Muestra datos básicos del admin
 * - Navega a gestión de usuarios y departamentos
 * - Permite salir (cerrar sesión)
 */
var saludoUsuario = document.getElementById("saludoUsuario");
var datosMostrar = document.getElementById("datosMostrar");
var btnSalir = document.getElementById("btnSalir");
var btnUsuarios = document.getElementById("btnUsuarios");
var btnDepartamentos = document.getElementById("btnDepartamentos");

// Botón salir -> invalida sesión
if (btnSalir) {
    btnSalir.addEventListener("click", () => { window.location.href = 'killSession'; });
}
// Ir a gestión de usuarios
if (btnUsuarios) {
    btnUsuarios.addEventListener("click", () => { window.location.href = '/control/usuarios'; });
}
// Ir a gestión de departamentos
if (btnDepartamentos) {
    btnDepartamentos.addEventListener("click", () => { window.location.href = '/departamentos'; });
}

// Cargar datos del admin autenticado
fetch('/admin')
    .then((response) => {
        if (response.ok) return response.json();
        throw new Error(`Error ${response.status}: ${response.statusText}`);
    })
    .then((data) => {
        const fecha = data.fechaCreacion ? new Date(data.fechaCreacion).toLocaleDateString() : '-';
        var tablaHTML = `
      <table class="tablaDatos">
        <thead>
          <tr>
            <th>ID Usuario</th>
            <th>Username</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Fecha Creación</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>${data.id}</td>
            <td>${data.username}</td>
            <td>${data.email ?? '-'}</td>
            <td>${data.rol}</td>
            <td>${fecha}</td>
          </tr>
        </tbody>
      </table>
    `;
        if (saludoUsuario) saludoUsuario.innerHTML = `Bienvenido de nuevo ${data.username}`;
        if (datosMostrar) datosMostrar.innerHTML = tablaHTML;
    })
    .catch((error) => console.error("Error al cargar datos del admin:", error));