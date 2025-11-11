document.addEventListener('DOMContentLoaded', () => {
    const body = document.body;
    const todosProdutos = JSON.parse(body.dataset.produtos || '[]');
    const venda = JSON.parse(body.dataset.venda || 'null');
    
    const produtoListContainer = document.getElementById('produtoList');
    const searchInput = document.getElementById('searchInput');
    const itensVendaBody = document.getElementById('itensVendaBody');
    const placeholderRow = document.getElementById('itens-venda-vazio');
    const valorTotalEl = document.getElementById('valorTotal');
    const form = document.getElementById('vendaForm');

    let itensAdicionados = new Set();

    const formatarMoeda = (valor) => {
        return (valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    };

    const renderizarListaProdutos = (filtro = '') => {
        produtoListContainer.innerHTML = '';
        const filtroLowerCase = filtro.toLowerCase();
        
        const produtosFiltrados = todosProdutos.filter(produto => 
            produto.nome.toLowerCase().includes(filtroLowerCase)
        );
        
        if (produtosFiltrados.length === 0) {
            produtoListContainer.innerHTML = '<p style="padding: 10px; text-align: center;">Nenhum produto encontrado.</p>';
            return;
        }

        produtosFiltrados.forEach(produto => {
            const isAdded = itensAdicionados.has(produto.id);
            const div = document.createElement('div');
            div.className = `insumo-item ${isAdded ? 'added' : ''}`;
            div.dataset.id = produto.id;
            div.innerHTML = `
                <span>${produto.nome} (${formatarMoeda(produto.precoVenda)})</span>
                <button type="button" class="add-item-btn" ${isAdded ? 'disabled' : ''}>Adicionar</button>
            `;
            produtoListContainer.appendChild(div);
        });
    };

    const adicionarItem = (produtoId, quantidadeInicial = 1, valorUnitarioInicial = -1) => {
        if (itensAdicionados.has(produtoId)) return;

        const produto = todosProdutos.find(p => p.id === produtoId);
        if (!produto) return;
        
        const valorVenda = (valorUnitarioInicial !== -1) ? valorUnitarioInicial : produto.precoVenda;

        if (placeholderRow) placeholderRow.style.display = 'none';

        const newRow = document.createElement('tr');
        newRow.dataset.id = produto.id;
        newRow.innerHTML = `
            <td>
                ${produto.nome}
                <input type="hidden" name="produtoId" value="${produto.id}">
            </td>
            <td><input type="number" name="quantidade" class="item-input" value="${quantidadeInicial}" min="1" step="1" required></td>
            <td><input type="number" name="valorUnitario" class="item-input" value="${valorVenda.toFixed(2)}" min="0.01" step="0.01" required></td>
            <td class="subtotal">R$ 0,00</td>
            <td class="item-actions">
                <button type="button" class="remove-item-btn"><i class='bx bxs-trash'></i></button>
            </td>
        `;

        itensVendaBody.appendChild(newRow);
        itensAdicionados.add(produtoId);
        renderizarListaProdutos(searchInput.value);
    };

    const removerItem = (produtoId) => {
        itensAdicionados.delete(produtoId);
        const rowToRemove = itensVendaBody.querySelector(`tr[data-id='${produtoId}']`);
        if (rowToRemove) {
            rowToRemove.remove();
        }
        
        if (itensAdicionados.size === 0 && placeholderRow) {
            placeholderRow.style.display = 'table-row';
        }

        calcularTotal();
        renderizarListaProdutos(searchInput.value);
    };

    const calcularTotal = () => {
        let totalGeral = 0;
        const rows = itensVendaBody.querySelectorAll("tr[data-id]");

        rows.forEach(row => {
            const quantidade = parseFloat(row.querySelector('input[name="quantidade"]').value) || 0;
            const valorUnitario = parseFloat(row.querySelector('input[name="valorUnitario"]').value) || 0;
            const subtotal = quantidade * valorUnitario;
            
            row.querySelector('.subtotal').textContent = formatarMoeda(subtotal);
            totalGeral += subtotal;
        });

        valorTotalEl.textContent = formatarMoeda(totalGeral);
    };

    const inicializarFormulario = () => {
        if (venda && venda.produtos) {
            if (venda.produtos.length > 0) {
                venda.produtos.forEach(item => {
                    adicionarItem(item.produtoId, item.quantidade, item.precoUnitario);
                });
            }
        } else {
            const hoje = new Date().toISOString().split('T')[0];
            document.getElementById('dataVenda').value = hoje;
        }
        calcularTotal();
        renderizarListaProdutos();
    };

    searchInput.addEventListener('keyup', () => renderizarListaProdutos(searchInput.value));

    produtoListContainer.addEventListener('click', function(e) {
        const addButton = e.target.closest('.add-item-btn');
        if (addButton) {
            const produtoId = parseInt(addButton.closest('.insumo-item').dataset.id, 10);
            adicionarItem(produtoId);
            calcularTotal();
        }
    });

    itensVendaBody.addEventListener('click', function(e) {
        const removeButton = e.target.closest('.remove-item-btn');
        if (removeButton) {
            const produtoId = parseInt(removeButton.closest('tr').dataset.id, 10);
            removerItem(produtoId);
        }
    });

    itensVendaBody.addEventListener('input', function(e) {
        if (e.target.classList.contains('item-input')) {
            calcularTotal();
        }
    });
    
    form.addEventListener('submit', function(e) {
        if (itensAdicionados.size === 0) {
            alert('Você precisa adicionar pelo menos um produto à venda.');
            e.preventDefault();
        }
    });

    inicializarFormulario();
});