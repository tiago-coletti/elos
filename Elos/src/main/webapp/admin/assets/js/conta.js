const pwShowHide = document.querySelectorAll(".showHidePw");
const pwFields = document.querySelectorAll(".password");
const form = document.querySelector("form");

const novaSenhaInput = document.getElementById("novaSenha");
const confirmarNovaSenhaInput = document.getElementById("confirmarNovaSenha");
const loginInput = document.getElementById("loginEmpreendimento");
const anoSemestreInput = document.getElementById("anoSemestre");
const phoneInput = document.getElementById('phoneNumber');

pwShowHide.forEach((eyeIcon) => {
    eyeIcon.addEventListener("click", () => {
        pwFields.forEach((pwField) => {
            if (pwField.type === "password") {
                pwField.type = "text";
                pwShowHide.forEach((icon) =>
                    icon.classList.replace("uil-eye-slash", "uil-eye")
                );
            } else {
                pwField.type = "password";
                pwShowHide.forEach((icon) =>
                    icon.classList.replace("uil-eye", "uil-eye-slash")
                );
            }
        });
    });
});

if (form) {
    form.addEventListener('submit', function(e) {
        const novaSenha = novaSenhaInput ? novaSenhaInput.value : '';
        const confirmarSenha = confirmarNovaSenhaInput ? confirmarNovaSenhaInput.value : '';

        if (novaSenha || confirmarSenha) {
            if (novaSenha !== confirmarSenha) {
                e.preventDefault();
                alert("Erro: A 'Nova Senha' e a 'Confirmação de Nova Senha' não coincidem.");
                confirmarNovaSenhaInput.focus();
                return;
            }
            if (novaSenha.length < 6) { 
                 e.preventDefault();
                 alert("Erro: A nova senha deve ter pelo menos 6 caracteres.");
                 novaSenhaInput.focus();
                 return;
            }
        }
    });
}

if (loginInput) {
    loginInput.addEventListener('keyup', function() {
        this.value = this.value.toLowerCase();
    });
}

if (anoSemestreInput) {
    anoSemestreInput.addEventListener('input', function(e) {
        let value = e.target.value;
        value = value.replace(/[^0-9/]/g, '');
        if (value.length === 4 && !value.includes('/')) {
            value = value + '/';
        }
        const parts = value.split('/');
        if (parts.length > 1 && parts[1].length > 1) {
            value = parts[0] + '/' + parts[1].substring(0, 1);
        }
        e.target.value = value.slice(0, 6);
    });
}

function handlePhoneInput(event) {
    const input = event.target;
    let value = input.value.replace(/\D/g, '').slice(0, 11);
    let maskedValue = "";
    if (value.length > 0) {
        maskedValue = "(" + value.substring(0, 2);
    }
    if (value.length > 2) {
        maskedValue = "(" + value.substring(0, 2) + ") " + value.substring(2, 3);
    }
    if (value.length > 3) {
        maskedValue += " " + value.substring(3, 7);
    }
    if (value.length > 7) {
        maskedValue += "-" + value.substring(7, 11);
    }
    input.value = maskedValue;
}

if (phoneInput) {
    phoneInput.addEventListener('input', handlePhoneInput);
    handlePhoneInput({ target: phoneInput });
}