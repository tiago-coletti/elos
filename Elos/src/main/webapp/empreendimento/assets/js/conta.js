/**
 * conta.js
 * Script para gerenciar interações na página de Conta/Perfil (conta.jsp).
 */

const pwShowHide = document.querySelectorAll(".showHidePw");
const pwFields = document.querySelectorAll(".password");
const form = document.querySelector("form");

// Campos de senha
const novaSenhaInput = document.getElementById("novaSenha");
const confirmarNovaSenhaInput = document.getElementById("confirmarNovaSenha");
const loginInput = document.getElementById("loginEmpreendimento");
const anoSemestreInput = document.getElementById("anoSemestre");

// =======================================================
// 1. Lógica de Alternância de Visibilidade da Senha
// (Reutilizada de login.js)
// =======================================================

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

// =======================================================
// 2. Validação de Alteração de Senha
// Garante que as novas senhas coincidam se alguma for preenchida.
// =======================================================

if (form) {
    form.addEventListener('submit', function(e) {
        const novaSenha = novaSenhaInput ? novaSenhaInput.value : '';
        const confirmarSenha = confirmarNovaSenhaInput ? confirmarNovaSenhaInput.value : '';

        // Se uma senha for preenchida, a outra também deve ser e devem ser iguais.
        if (novaSenha || confirmarSenha) {
            if (novaSenha !== confirmarSenha) {
                e.preventDefault();
                alert("Erro: A 'Nova Senha' e a 'Confirmação de Nova Senha' não coincidem.");
                confirmarNovaSenhaInput.focus();
                return;
            }
            
            // Requisito adicional: verifica se a nova senha tem um comprimento mínimo.
            if (novaSenha.length < 6) { 
                 e.preventDefault();
                 alert("Erro: A nova senha deve ter pelo menos 6 caracteres.");
                 novaSenhaInput.focus();
                 return;
            }
            
        } else if (novaSenha && !confirmarSenha) {
            // Caso raro onde só a nova senha é preenchida
            e.preventDefault();
            alert("Erro: Preencha a 'Confirmação de Nova Senha'.");
            confirmarNovaSenhaInput.focus();
            return;
        } else if (!novaSenha && confirmarSenha) {
            // Caso raro onde só a confirmação é preenchida
            e.preventDefault();
            alert("Erro: Preencha a 'Nova Senha'.");
            novaSenhaInput.focus();
            return;
        }
        
        // Se a validação passar, o formulário será submetido normalmente.
    });
}

// =======================================================
// 3. Formatação de Campos
// =======================================================

// Força minúsculas no Login (mesmo sendo readonly, por segurança)
if (loginInput) {
    loginInput.addEventListener('keyup', function() {
        this.value = this.value.toLowerCase();
    });
}

// Força padrão no Semestre (YYYY/S) - apenas se o campo existir
const semestreRegex = /\/([1-2])$/; 

if (anoSemestreInput) {
    anoSemestreInput.addEventListener('input', function() {
        let value = this.value;
        
        // 1. Permite apenas números e a barra (/)
        value = value.replace(/[^0-9/]/g, '');

        // 2. Garante o formato AAAA/S
        if (value.length > 4 && value[4] !== '/') {
            value = value.slice(0, 4) + '/' + value.slice(4).replace('/', '');
        } else if (value.length >= 7) {
            // Limita a 7 caracteres (AAAA/S)
            value = value.slice(0, 7);
            
            // Corrige o dígito do semestre
            const match = value.match(semestreRegex);
            if (match) {
                const semesterDigit = match[1];
                if (semesterDigit > 2) {
                    value = value.slice(0, 6) + '2'; // Limita a 2
                }
            } else if (value.length === 6 && value[5] !== '1' && value[5] !== '2') {
                 // Força o dígito do semestre se houver erro
                 value = value.slice(0, 5) + '1';
            }
        }
        
        this.value = value;
    });
}