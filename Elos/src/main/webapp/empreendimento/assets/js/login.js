const container = document.querySelector(".container"),
  pwShowHide = document.querySelectorAll(".showHidePw"),
  pwFields = document.querySelectorAll(".password"),
  signUp = document.querySelector(".signup-link"),
  login = document.querySelector(".login-link"),
  backBtn = document.querySelector(".back-btn button");

// Lógica de alternância de visibilidade da senha
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

// Lógica de alternância para a tela de Cadastro
if (signUp) {
  signUp.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.add("active");
  });
}

if (login) {
  login.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.remove("active");
  });
}

// Lógica do botão "Voltar"
if (backBtn) {
  backBtn.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.remove("active");
  });
}

// --- Lógica de Empreendimento Solidário e Alunos ---

const alunoSolidarioCheck = document.getElementById("alunoSolidarioCheck");
const tipoEmpreendimentoInput = document.getElementById("tipoEmpreendimento");
const alunosSection = document.getElementById("alunos-section");
const addAlunoBtn = document.getElementById("add-aluno-btn");
const alunosList = document.getElementById("alunos-list");
let alunoCount = 0;

function addAlunoField() {
    alunoCount++;
    
    const alunoDiv = document.createElement('div');
    alunoDiv.classList.add('fields-group', 'aluno-field');
    alunoDiv.innerHTML = `
        <div class="input-field">
            <label for="aluno-nome-${alunoCount}">Nome do Integrante</label>
            <input type="text" id="aluno-nome-${alunoCount}" placeholder="Nome Completo" 
                   name="alunos[${alunoCount}].nome" required />
        </div>
        <div class="input-field">
            <label for="aluno-matricula-${alunoCount}">Matrícula/RA</label>
            <input type="text" id="aluno-matricula-${alunoCount}" placeholder="Matrícula/RA" 
                   name="alunos[${alunoCount}].matricula" required />
        </div>
        
        <div class="input-field remove-button">
            <button type="button" class="remove-aluno-btn uil uil-trash-alt" title="Remover Integrante"></button>
        </div>
    `;

    alunosList.appendChild(alunoDiv);

    alunoDiv.querySelector('.remove-aluno-btn').addEventListener('click', function() {
        alunoDiv.remove();
    });
}

if (alunoSolidarioCheck) {
    alunoSolidarioCheck.addEventListener("change", function() {
        const cursoGeralId = document.getElementById("cursoGeralId");
        const anoSemestre = document.getElementById("anoSemestre");

        if (this.checked) {
            alunosSection.style.display = "block";
            tipoEmpreendimentoInput.value = "ALUNO_SOLIDARIO";
            cursoGeralId.setAttribute('required', 'required');
            anoSemestre.setAttribute('required', 'required');
            
            if (alunosList.children.length === 0) {
                addAlunoField(); 
            }
        } else {
            alunosSection.style.display = "none";
            tipoEmpreendimentoInput.value = "PADRAO";
            cursoGeralId.removeAttribute('required');
            anoSemestre.removeAttribute('required');
        }
    });
}

if (addAlunoBtn) {
    addAlunoBtn.addEventListener("click", addAlunoField);
}

if (alunoSolidarioCheck && !alunoSolidarioCheck.checked) {
    alunosSection.style.display = "none";
    document.getElementById("cursoGeralId").removeAttribute('required');
    document.getElementById("anoSemestre").removeAttribute('required');
}

// --- CÓDIGO DE FORÇAR MINÚSCULAS NO LOGIN ---

const loginInput = document.getElementById("loginEmpreendimento");

if (loginInput) {
    loginInput.addEventListener('keyup', function() {
        this.value = this.value.toLowerCase();
    });
}

// --- CÓDIGO DE FORÇAR PADRÃO NO SEMESTRE (YYYY/S) ---

const anoSemestreInput = document.getElementById("anoSemestre");
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