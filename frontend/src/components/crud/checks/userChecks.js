export default function userChecks(user, user1) {
    let message;
    if (/^\d+$/.test(user.username)) {
        message=("El username no puede contener solo números");
        return message;
    }

    if (user.password !== user1.password && (user.password.length < 7 || user.password.length > 15)) {
        message="La contraseña debe tener entre 7 y 15 caracteres";
        return message; 
    }

    if (user.password !== user1.password && !/[!@#$%^&*(),.?":{}|<>]/.test(user.password)) {
        message= "La contraseña debe contener al menos un carácter especial o signo de puntuación";
        return message; 
    }

    if (user.password !== user1.password && !/\d/.test(user.password)) {
        message= "La contraseña debe contener al menos un número";
        return message; 
    }

}