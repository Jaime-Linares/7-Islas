export const formValidators = {
    notEmptyValidator: {
        validate: (value) => {
            return value.trim().length > 0;
        },
        message: "The field cannot be empty"
    },
    telephoneValidator: {
        validate: (value) => {
            return value.trim().length === 9 && /^\d+$/.test(value);
        },
        message: "The telephone number must be 9 digits long and contain only numbers"
    },
    notNoneTypeValidator: {
        validate: (value) => {
            return value !== "None";
        },
        message: "Please, select a type"
    },
    validPhoneNumberValidator: {
        validate: (value) => {
            return value.trim().length === 9 && /^\d+$/.test(value);
        },
        message: "The phone number must be 9 digits long and contain only numbers"
    },
    emailValidator: {
        validate: (value) => {
          const emailPattern = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
          return emailPattern.test(value);
        },
        message: "Please enter a valid email address."
    },
    enoughAgeForRegister: {
        validate: (value) => {
            const birthDate = new Date(value);
            const today = new Date();
            let age = today.getFullYear() - birthDate.getFullYear();
    
            if (today < new Date(today.getFullYear(), birthDate.getMonth(), birthDate.getDate())) {
                age--;
            }
            return age>12;
        },
        message: "you must be over 12 years old"

    }
}