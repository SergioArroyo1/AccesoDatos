formulario = document.getElementById("formulario");
username = document.getElementById("username");
password = document.getElementById("password");
fetch("/killSession")

formulario.addEventListener("submit",(e) =>{
    e.preventDefault();

    usuario = username.value.trim();
    contrasena = password.value.trim();

    fetch("/comprobar",{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            username: usuario,
            password: contrasena
        })
    })
        .then((response) =>{
            if (response.ok) {
                return response.json();
            }
            else if(response.status === 401){
                throw new Error("Credenciales Incorrectas")
            }
            else {
                throw new Error("Error en el servidor")
            }
        })
        .then((data) => {
            console.log(data);
            window.location.href = '/control'
        })
        .catch((error)=>{
            console.error("Fallo en el login: ", error);
        });
});