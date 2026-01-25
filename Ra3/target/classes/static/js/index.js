/**
 * JS de la página de login/registro (index.html estático).
 * - Envía login por AJAX a /loginweb
 * - Redirige a /registro al pulsar "Registrarse"
 */
document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.getElementById("formulario");
    const username = document.getElementById("username");
    const password = document.getElementById("password");
    const btnRegistro = document.getElementById("btnRegistro");

    // Login vía submit del formulario
    if (formulario) {
        formulario.addEventListener("submit", (e) => {
            e.preventDefault();

            const usuarioUsername = username.value.trim();
            const usuarioPassword = password.value.trim();

            if (!usuarioUsername || !usuarioPassword) {
                alert("Introduce username y password.");
                return;
            }

            fetch("/loginweb", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    username: usuarioUsername,
                    password: usuarioPassword,
                }),
            })
                .then((response) => {
                    if (response.ok) return response.json(); // UsuarioDTO
                    if (response.status === 401) throw new Error("Credenciales Incorrectas");
                    throw new Error(`Error ${response.status}: ${response.statusText}`);
                })
                .then(() => {
                    // Login correcto: el backend guarda el DTO en sesión
                    window.location.href = "/control";
                })
                .catch((error) => {
                    console.log("Fallo en login: ", error);

                    const mensajeError =
                        !navigator.onLine
                            ? "Sin conexión. Revisa tu red."
                            : error.message === "Credenciales Incorrectas"
                                ? "Usuario o contraseña incorrectos"
                                : "No se pudo iniciar sesión. Inténtalo de nuevo.";

                    alert(mensajeError);
                    password.value = "";
                    password.focus();
                });
        });
    }

    // Botón Registrarse -> página de registro (Thymeleaf)
    if (btnRegistro) {
        btnRegistro.addEventListener("click", () => {
            window.location.href = "/registro";
        });
    }
});