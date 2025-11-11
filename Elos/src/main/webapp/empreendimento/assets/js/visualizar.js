let modal, formAjuste, inputId, inputQtd, maxLabel;

function abrirModalAjuste(button) {
  const loteId = button.getAttribute('data-lote-id');
  const maxQtd = button.getAttribute('data-lote-max');
  
  inputId.value = loteId;
  inputQtd.value = '';
  inputQtd.max = maxQtd;
  maxLabel.textContent = `Disponível neste lote: ${parseFloat(maxQtd).toFixed(3)}`;
  
  if (modal) {
    modal.style.display = 'flex';
  }
}

function fecharModalAjuste() {
  if (modal) {
    modal.style.display = 'none';
  }
}

document.addEventListener('DOMContentLoaded', function() {
  
  modal = document.getElementById('modal-ajuste');
  formAjuste = document.getElementById('form-ajuste');
  inputId = document.getElementById('modal-compra-insumo-id');
  inputQtd = document.getElementById('modal-quantidade');
  maxLabel = document.getElementById('modal-max-label');

  if (formAjuste) {
    formAjuste.addEventListener('submit', function(event) {
      const quantidadeDigitada = parseFloat(inputQtd.value);
      const quantidadeMaxima = parseFloat(inputQtd.max);

      if (isNaN(quantidadeDigitada) || quantidadeDigitada <= 0) {
        alert('Por favor, insira uma quantidade válida maior que zero.');
        event.preventDefault();
        return;
      }

      if (quantidadeDigitada > quantidadeMaxima) {
        alert(`A quantidade a baixar (${quantidadeDigitada}) não pode ser maior que a quantidade disponível neste lote (${quantidadeMaxima}).`);
        event.preventDefault();
        return;
      }
    });
  }

  if (modal) {
    window.onclick = function(event) {
      if (event.target == modal) {
        fecharModalAjuste();
      }
    }
  }
});