// empreendimento/assets/js/toast.js

document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Procura o div gatilho que o JSP criou
    const messageTrigger = document.getElementById('sessionMessage');
    
    if (messageTrigger) {
        // 2. Pega a mensagem de dentro do atributo 'data-message'
        const message = messageTrigger.dataset.message;

        if (message) {
            // 3. Pega os elementos do toast "molde"
            const toastContainer = document.getElementById('toast-container');
            const toastMessageElement = document.getElementById('toast-message-content');
            const toastCloseBtn = document.getElementById('toast-close-btn');

            if (!toastContainer || !toastMessageElement || !toastCloseBtn) {
                console.error("Elementos do Toast (container, message, close) não encontrados no DOM.");
                return;
            }

            // 4. Determina o tipo de toast (success, error, info)
            let toastType = 'info'; // Padrão
            const lowerCaseMessage = message.toLowerCase();

            if (lowerCaseMessage.includes('sucesso')) {
                toastType = 'success';
            } else if (lowerCaseMessage.includes('erro') || lowerCaseMessage.includes('falha') || lowerCaseMessage.includes('inválido')) {
                toastType = 'error';
            }
            
            // 5. Configura e exibe o toast
            toastMessageElement.textContent = message;
            
            // Limpa classes antigas e adiciona as novas
            toastContainer.classList.remove('info', 'success', 'error');
            toastContainer.classList.add(toastType);
            toastContainer.classList.add('show'); // <-- Esta é a classe que ativa a animação

            // 6. Define um timer para esconder o toast (ex: 5 segundos)
            const autoHideTimer = setTimeout(() => {
                hideToast(toastContainer);
            }, 5000); // 5000ms = 5 segundos

            // 7. Adiciona evento de clique no botão de fechar
            toastCloseBtn.onclick = () => {
                clearTimeout(autoHideTimer); // Cancela o timer se fechar manualmente
                hideToast(toastContainer);
            };
        }
    }
});

// Função auxiliar para esconder o toast
function hideToast(toastContainer) {
    toastContainer.classList.remove('show');
}