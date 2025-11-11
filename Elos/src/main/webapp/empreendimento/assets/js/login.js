const container = document.querySelector(".container"),
  pwShowHide = document.querySelectorAll(".showHidePw"),
  pwFields = document.querySelectorAll(".password"),
  signUp = document.querySelector(".signup-link"),
  login = document.querySelector(".login-link"),
  backBtn = document.querySelector(".back-btn button");

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

if (backBtn) {
  backBtn.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.remove("active");
  });
}

const alunoSolidarioCheck = document.getElementById("alunoSolidarioCheck");
const tipoEmpreendimentoInput = document.getElementById("tipoEmpreendimento");
const alunosSection = document.getElementById("alunos-section");
const addAlunoBtn = document.getElementById("add-aluno-btn");
const alunosList = document.getElementById("alunos-list");
let alunoCount = 0;

function addAlunoField() {
    alunoCount++;
    
    const isRequired = alunoSolidarioCheck.checked ? 'required' : '';

    const alunoDiv = document.createElement('div');
    alunoDiv.classList.add('fields-group', 'aluno-field');
    alunoDiv.innerHTML = `
        <div class="input-field">
            <label for="aluno-nome-${alunoCount}">Nome do Integrante</label>
            <input type="text" id="aluno-nome-${alunoCount}" placeholder="Nome Completo" 
                   name="alunos[${alunoCount}].nome" ${isRequired} />
        </div>
        <div class="input-field">
            <label for="aluno-matricula-${alunoCount}">Matrícula/RA</label>
            <input type="text" id="aluno-matricula-${alunoCount}" placeholder="Matrícula/RA" 
                   name="alunos[${alunoCount}].matricula" ${isRequired} />
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
        const allAlunoInputs = alunosList.querySelectorAll('input');

        if (this.checked) {
            alunosSection.style.display = "block";
            tipoEmpreendimentoInput.value = "ALUNO_SOLIDARIO";
            cursoGeralId.setAttribute('required', 'required');
            anoSemestre.setAttribute('required', 'required');
            
            if (alunosList.children.length === 0) {
                addAlunoField(); 
            } else {
                allAlunoInputs.forEach(input => input.setAttribute('required', 'required'));
            }
        } else {
            alunosSection.style.display = "none";
            tipoEmpreendimentoInput.value = "PADRAO";
            cursoGeralId.removeAttribute('required');
            anoSemestre.removeAttribute('required');
            
            allAlunoInputs.forEach(input => input.removeAttribute('required'));
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

const loginInput = document.getElementById("loginEmpreendimento");

if (loginInput) {
    loginInput.addEventListener('keyup', function() {
        this.value = this.value.toLowerCase();
    });
}

const anoSemestreInput = document.getElementById("anoSemestre");
const semestreRegex = /\/([1-2])$/; 

if (anoSemestreInput) {
    anoSemestreInput.addEventListener('input', function() {
        let value = this.value;
        
        value = value.replace(/[^0-9/]/g, '');

        if (value.length > 4 && value[4] !== '/') {
            value = value.slice(0, 4) + '/' + value.slice(4).replace('/', '');
        } else if (value.length >= 7) {
            value = value.slice(0, 7);
            
            const match = value.match(semestreRegex);
            if (match) {
                const semesterDigit = match[1];
                if (semesterDigit > 2) {
                    value = value.slice(0, 6) + '2';
                }
            } else if (value.length === 6 && value[5] !== '1' && value[5] !== '2') {
                 value = value.slice(0, 5) + '1';
            }
        }
        
        this.value = value;
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const phoneInput = document.getElementById('phoneNumber');

    if (phoneInput) {
        phoneInput.addEventListener('input', handlePhoneInput);
    }
});

function handlePhoneInput(event) {
    const input = event.target;
    let value = input.value.replace(/\D/g, '');

    value = value.slice(0, 11);

    if (value.length > 7) {
        input.value = `(${value.slice(0, 2)}) ${value.slice(2, 3)} ${value.slice(3, 7)}-${value.slice(7)}`;
    } else if (value.length > 3) {
        input.value = `(${value.slice(0, 2)}) ${value.slice(2, 3)} ${value.slice(3)}`;
    } else if (value.length > 2) {
        input.value = `(${value.slice(0, 2)}) ${value.slice(2)}`;
    } else {
        input.value = value.replace(/^(\d{2})/, '($1'); 
    }
}