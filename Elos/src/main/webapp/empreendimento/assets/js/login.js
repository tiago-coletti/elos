const container = document.querySelector(".container"),
  pwShowHide = document.querySelectorAll(".showHidePw"),
  pwFields = document.querySelectorAll(".password"),
  signUp = document.querySelector(".signup-link"),
  login = document.querySelector(".login-link"),
  backBtn = document.querySelector(".back-btn button");

// Alternar visibilidade da senha
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

// Clicar em "Signup Now"
if (signUp) {
  signUp.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.add("active");
  });
}

// Clicar em "Faça login" (se existir)
if (login) {
  login.addEventListener("click", (e) => {
    e.preventDefault();
    container.classList.remove("active");
  });
}

// Clicar no botão "Voltar"
if (backBtn) {
  backBtn.addEventListener("click", (e) => {
    console.log("Botão Voltar clicado!");
    e.preventDefault();
    container.classList.remove("active");
  });
}
