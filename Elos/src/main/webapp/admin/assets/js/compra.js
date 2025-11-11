document.addEventListener('DOMContentLoaded', function() {
    // 1. DADOS INICIAIS
    const body = document.body;
    const insumos = JSON.parse(body.dataset.insumos || '[]');
    const compraParaEditar = JSON.parse(body.dataset.compra || 'null'); // <-- LÊ OS DADOS DA COMPRA

    // 2. ELEMENTOS DO DOM
    const insumoListContainer = document.getElementById('insumoList');
    const itensCompraBody = document.getElementById('itensCompraBody');
    const searchInput = document.getElementById('searchInput');
    const valorTotalEl = document.getElementById('valorTotal');
    const form = document.getElementById('compraForm');
    const placeholderRow = document.getElementById('itens-compra-vazio');
    
    // 3. ESTADO DA APLICAÇÃO
    let itensAdicionados = new Set();

    // 4. FUNÇÕES
    
    function renderInsumoList(filter = '') {
        insumoListContainer.innerHTML = '';
        const filteredInsumos = insumos.filter(insumo => 
            insumo.nome.toLowerCase().includes(filter.toLowerCase())
        );

        if (filteredInsumos.length === 0) {
            insumoListContainer.innerHTML = '<p style="padding: 10px; text-align: center;">Nenhum insumo encontrado.</p>';
            return;
        }

        filteredInsumos.forEach(insumo => {
            const isAdded = itensAdicionados.has(insumo.id);
            const div = document.createElement('div');
            div.className = `insumo-item ${isAdded ? 'added' : ''}`;
            div.dataset.id = insumo.id;
            div.innerHTML = `
                <span>${insumo.nome} (${insumo.unidadeMedida})</span>
                <button type="button" class="add-item-btn" ${isAdded ? 'disabled' : ''}>Adicionar</button>
            `;
            insumoListContainer.appendChild(div);
        });
    }

    // MUDANÇA: A função agora aceita valores iniciais para quantidade e preço
    function adicionarItem(insumoId, quantidadeInicial = 1, valorUnitarioInicial = 0) {
        if (itensAdicionados.has(insumoId)) return;

        const insumo = insumos.find(i => i.id === insumoId);
        if (!insumo) return;

        if (placeholderRow) placeholderRow.style.display = 'none';

        const newRow = document.createElement('tr');
        newRow.dataset.id = insumo.id;
        newRow.innerHTML = `
            <td>
                ${insumo.nome}
                <input type="hidden" name="insumoId" value="${insumo.id}">
            </td>
            <td><input type="number" name="quantidade" class="item-input" value="${quantidadeInicial}" min="0.01" step="0.01" required></td>
            <td><input type="number" name="valorUnitario" class="item-input" value="${valorUnitarioInicial.toFixed(2)}" min="0.01" step="0.01" required></td>
            <td class="subtotal">R$ 0,00</td>
            <td class="item-actions"><button type="button" class="remove-item-btn"><i class='bx bxs-trash'></i></button></td>
        `;

        itensCompraBody.appendChild(newRow);
        itensAdicionados.add(insumoId);
        renderInsumoList(searchInput.value);
    }

    function removerItem(insumoId) {
        itensAdicionados.delete(insumoId);
        const rowToRemove = itensCompraBody.querySelector(`tr[data-id='${insumoId}']`);
        if (rowToRemove) {
            rowToRemove.remove();
        }
        
        if (itensAdicionados.size === 0 && placeholderRow) {
            placeholderRow.style.display = 'table-row';
        }

        calcularTotal();
        renderInsumoList(searchInput.value);
    }

    function calcularTotal() {
        let totalGeral = 0;
        const rows = itensCompraBody.querySelectorAll("tr[data-id]");

        rows.forEach(row => {
            const quantidade = parseFloat(row.querySelector('input[name="quantidade"]').value) || 0;
            const valorUnitario = parseFloat(row.querySelector('input[name="valorUnitario"]').value) || 0;
            const subtotal = quantidade * valorUnitario;
            
            row.querySelector('.subtotal').textContent = `R$ ${subtotal.toFixed(2).replace('.', ',')}`;
            totalGeral += subtotal;
        });

        valorTotalEl.textContent = `R$ ${totalGeral.toFixed(2).replace('.', ',')}`;
    }
    
    // NOVO: Função para inicializar o formulário
    function inicializarFormulario() {
        if (compraParaEditar) { // Se estamos na página de edição
            // Popula a tabela com os itens existentes da compra
            if (compraParaEditar.itens && compraParaEditar.itens.length > 0) {
                compraParaEditar.itens.forEach(item => {
                    adicionarItem(item.insumoId, item.quantidadeComprada, item.precoUnitario);
                });
            }
        } else { // Se estamos na página de inclusão
            document.getElementById('dataCompra').valueAsDate = new Date();
        }
        calcularTotal(); // Calcula o total inicial
        renderInsumoList();
    }

    // 5. EVENT LISTENERS
    searchInput.addEventListener('keyup', () => renderInsumoList(searchInput.value));

    insumoListContainer.addEventListener('click', function(e) {
        const addButton = e.target.closest('.add-item-btn');
        if (addButton) {
            const insumoId = parseInt(addButton.closest('.insumo-item').dataset.id, 10);
            adicionarItem(insumoId); // Adiciona com valores padrão
            calcularTotal();
        }
    });

    itensCompraBody.addEventListener('click', function(e) {
        const removeButton = e.target.closest('.remove-item-btn');
        if (removeButton) {
            const insumoId = parseInt(removeButton.closest('tr').dataset.id, 10);
            removerItem(insumoId);
        }
    });

    itensCompraBody.addEventListener('input', function(e) {
        if (e.target.classList.contains('item-input')) {
            calcularTotal();
        }
    });
    
    form.addEventListener('submit', function(e) {
        if (itensAdicionados.size === 0) {
            alert('Você precisa adicionar pelo menos um insumo à compra.');
            e.preventDefault();
        }
    });

    // 6. INICIALIZAÇÃO
    inicializarFormulario(); // Chama a nova função de inicialização
});