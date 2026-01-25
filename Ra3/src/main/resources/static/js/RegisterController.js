/**
 * JS de la página de registro.
 * - Valida campos
 * - Envía POST /registro
 * - Muestra mensajes y redirige a login al crear correctamente
 */
document.addEventListener("DOMContentLoaded", () => {
    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const btnRegistrar = document.getElementById("btnRegistrar");
    const btnVolver = document.getElementById("btnVolver");
    const msg = document.getElementById("msg");

    function showMsg(text, type = "error") {
        msg.textContent = text;
        msg.className = type === "success" ? "success" : "error";
    }

    // Volver al index (login)
    if (btnVolver) {
        btnVolver.addEventListener("click", () => {
            window.location.href = "/index.html";
        });
    }

    // Registrar nuevo usuario (rol user por defecto)
    if (btnRegistrar) {
        btnRegistrar.addEventListener("click", () => {
            const payload = {
                username: username.value.trim(),
                email: email.value.trim(),
                password: password.value.trim(),
            };

            if (!payload.username || !payload.email || !payload.password) {
                showMsg("Todos los campos son obligatorios.");
                return;
            }

            fetch("/registro", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            })
                .then(res => {
                    if (res.ok) return res.json();
                    if (res.status === 400) throw new Error("Datos inválidos (revisa username/email/password).");
                    if (res.status === 409) throw new Error("Username o email ya están en uso.");
                    throw new Error(`Error ${res.status}: ${res.statusText}`);
                })
                .then(data => {
                    showMsg("Usuario registrado correctamente. Redirigiendo a login...", "success");
                    setTimeout(() => window.location.href = "/index.html", 1200);
                })
                .catch(err => showMsg(err.message || "No se pudo registrar el usuario."));
        });
    }
});