export default function PlayerRegisterChecks(playerDTO) {
    let message;
    if (/^\d+$/.test(playerDTO.username)) {
        message=("El username no puede contener solo números");
        return message;
    }

    if (playerDTO.password.length < 7 || playerDTO.password.length > 15) {
        message="La contraseña debe tener entre 7 y 15 caracteres";
        return message; 
    }

    if (!/[!@#$%^&*(),.?":{}|<>]/.test(playerDTO.password)) {
        message= "La contraseña debe contener al menos un carácter especial o signo de puntuación";
        return message; 
    }

    if (!/\d/.test(playerDTO.password)) {
        message= "La contraseña debe contener al menos un número";
        return message; 
    }

    if (/\d/.test(playerDTO.firstName)) {
        message=("El nombre no puede contener números");
        return message; 
    } 
    
    if (/\d/.test(playerDTO.lastName)) {
        message=("El apellido no puede contener números");
        return message; 
    } 
    
    const currentDate = new Date();
    const minAgeDate = new Date();
    minAgeDate.setFullYear(currentDate.getFullYear() - 12);
    if (new Date(playerDTO.birthdayDate) > minAgeDate) {
        message="Debes tener al menos 12 años para registrarte.";
        return message;
    }

    const emailRegex = /^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(playerDTO.email)) {
        message="Introduce un correo electrónico válido.";
        return message;
    }
}