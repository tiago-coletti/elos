document.addEventListener('DOMContentLoaded', function() {
    // 1. DADOS INICIAIS
    const body = document.body;
    const produtoData = JSON.parse(body.dataset.produto || 'null'); // NOVO: Pega os dados do produto a ser editado
    const insumosData = JSON.parse(body.dataset.insumos || '[]');
    const maosObraData = JSON.parse(body.dataset.maosObra || '[]');

    // 2. ELEMENTOS DO DOM GERAIS
    const form = document.getElementById('produtoForm');
    const custoTotalEl = document.getElementById('custoTotalProduto');

    // -- Elementos de Insumos
    const searchInputInsumo = document.getElementById('searchInputInsumo');
    const listaInsumosDisponiveis = document.getElementById('listaInsumosDisponiveis');
    const itensInsumoBody = document.getElementById('itensInsumoBody');
    const placeholderInsumo = document.getElementById('itens-insumo-vazio');

    // -- Elementos de Mão de Obra
    const searchInputMaoObra = document.getElementById('searchInputMaoObra');
    const listaMaosObraDisponiveis = document.getElementById('listaMaosObraDisponiveis');
    const itensMaoObraBody = document.getElementById('itensMaoObraBody');
    const placeholderMaoObra = document.getElementById('itens-maoobra-vazio');

    // 3. ESTADO DA APLICAÇÃO
    let insumosAdicionados = new Set();
    let maosObraAdicionadas = new Set();

    // 4. FUNÇÕES GERAIS
    function formatCurrency(value) {
        const numberValue = Number(value) || 0;
        return `R$ ${numberValue.toFixed(2).replace('.', ',')}`;
    }

    function calcularCustoTotal() {
        let custoTotal = 0;

        itensInsumoBody.querySelectorAll("tr[data-id]").forEach(row => {
            const quantidade = parseFloat(row.querySelector('input[name="insumoQuantidade"]').value) || 0;
            const valorUnitario = parseFloat(row.querySelector('.custo-unitario').dataset.custo || 0);
            const subtotal = quantidade * valorUnitario;
            row.querySelector('.subtotal').textContent = formatCurrency(subtotal);
            custoTotal += subtotal;
        });

        itensMaoObraBody.querySelectorAll("tr[data-id]").forEach(row => {
            const horas = parseFloat(row.querySelector('input[name="maoObraHoras"]').value) || 0;
            const custoHora = parseFloat(row.querySelector('.custo-hora').dataset.custo || 0);
            const subtotal = horas * custoHora;
            row.querySelector('.subtotal').textContent = formatCurrency(subtotal);
            custoTotal += subtotal;
        });

        custoTotalEl.textContent = formatCurrency(custoTotal);
    }
    
    // 5. FUNÇÕES ESPECÍFICAS PARA INSUMOS
    function renderInsumoList(filter = '') {
        listaInsumosDisponiveis.innerHTML = '';
        const filtered = insumosData.filter(i => 
            i.nome.toLowerCase().includes(filter.toLowerCase())
        );

        if (filtered.length === 0) {
            listaInsumosDisponiveis.innerHTML = '<p style="padding: 10px; text-align: center;">Nenhum insumo encontrado.</p>';
            return;
        }

        filtered.forEach(insumo => {
            const isAdded = insumosAdicionados.has(insumo.id);
            const div = document.createElement('div');
            div.className = `insumo-item ${isAdded ? 'added' : ''}`;
            div.dataset.id = insumo.id;
            div.innerHTML = `
                <span>${insumo.nome} (${insumo.unidadeMedida})</span>
                <button type="button" class="add-item-btn" ${isAdded ? 'disabled' : ''}>Adicionar</button>
            `;
            listaInsumosDisponiveis.appendChild(div);
        });
    }

    // ALTERADO: A função agora aceita a quantidade como parâmetro opcional
    function adicionarInsumo(insumoId, quantidade = 1.00) {
        if (insumosAdicionados.has(insumoId)) return;
        const insumo = insumosData.find(i => i.id === insumoId);
        if (!insumo) return;
        
        const custoInicial = (typeof insumo.custoEstimado === 'number') ? insumo.custoEstimado : 0;

        if (placeholderInsumo) placeholderInsumo.style.display = 'none';
        
        const newRow = document.createElement('tr');
        newRow.dataset.id = insumo.id;

        // ALTERADO: O valor do input de quantidade agora usa o parâmetro recebido
        newRow.innerHTML = `
            <td>
                ${insumo.nome}
                <input type="hidden" name="insumoId" value="${insumo.id}">
            </td>
            <td><input type="number" name="insumoQuantidade" class="item-input" value="${quantidade.toFixed(2)}" min="0.01" step="0.01" required></td>
            <td class="custo-unitario" data-custo="${custoInicial}">${formatCurrency(custoInicial)}</td>
            <td class="subtotal">R$ 0,00</td>
            <td class="item-actions"><button type="button" class="remove-item-btn"><i class='bx bxs-trash'></i></button></td>
        `;
        itensInsumoBody.appendChild(newRow);
        insumosAdicionados.add(insumoId);
        renderInsumoList(searchInputInsumo.value);
    }

    function removerInsumo(insumoId) {
        insumosAdicionados.delete(insumoId);
        const row = itensInsumoBody.querySelector(`tr[data-id='${insumoId}']`);
        if (row) row.remove();
        if (insumosAdicionados.size === 0 && placeholderInsumo) {
            placeholderInsumo.style.display = 'table-row';
        }
        renderInsumoList(searchInputInsumo.value);
        calcularCustoTotal();
    }
    
    // 6. FUNÇÕES ESPECÍFICAS PARA MÃO DE OBRA
    function renderMaoObraList(filter = '') {
        listaMaosObraDisponiveis.innerHTML = '';
        const filtered = maosObraData.filter(m => 
            m.nome && m.nome.toLowerCase().includes(filter.toLowerCase())
        );

        if (filtered.length === 0) {
            listaMaosObraDisponiveis.innerHTML = '<p style="padding: 10px; text-align: center;">Nenhuma mão de obra encontrada.</p>';
            return;
        }

        filtered.forEach(maoObra => {
            const isAdded = maosObraAdicionadas.has(maoObra.id);
            const div = document.createElement('div');
            div.className = `insumo-item ${isAdded ? 'added' : ''}`;
            div.dataset.id = maoObra.id;
            div.innerHTML = `
                <span>${maoObra.nome}</span>
                <button type="button" class="add-item-btn" ${isAdded ? 'disabled' : ''}>Adicionar</button>
            `;
            listaMaosObraDisponiveis.appendChild(div);
        });
    }
    
    // ALTERADO: A função agora aceita as horas como parâmetro opcional
    function adicionarMaoObra(maoObraId, horas = 1.00) {
        if (maosObraAdicionadas.has(maoObraId)) return;
        const maoObra = maosObraData.find(m => m.id === maoObraId);
        if (!maoObra) return;
        const custoHoraInicial = (typeof maoObra.custoHora === 'number') ? maoObra.custoHora : 0;
        if (placeholderMaoObra) placeholderMaoObra.style.display = 'none';
        const newRow = document.createElement('tr');
        newRow.dataset.id = maoObra.id;
        
        // ALTERADO: O valor do input de horas agora usa o parâmetro recebido
        newRow.innerHTML = `
            <td>
                ${maoObra.nome}
                <input type="hidden" name="maoObraId" value="${maoObra.id}">
            </td>
            <td class="custo-hora" data-custo="${custoHoraInicial}">${formatCurrency(custoHoraInicial)}</td>
            <td><input type="number" name="maoObraHoras" class="item-input" value="${horas.toFixed(2)}" min="0.01" step="0.01" required></td>
            <td class="subtotal">R$ 0,00</td>
            <td class="item-actions"><button type="button" class="remove-item-btn"><i class='bx bxs-trash'></i></button></td>
        `;
        itensMaoObraBody.appendChild(newRow);
        maosObraAdicionadas.add(maoObraId);
        renderMaoObraList(searchInputMaoObra.value);
    }

    function removerMaoObra(maoObraId) {
        maosObraAdicionadas.delete(maoObraId);
        const row = itensMaoObraBody.querySelector(`tr[data-id='${maoObraId}']`);
        if (row) row.remove();
        if (maosObraAdicionadas.size === 0 && placeholderMaoObra) {
            placeholderMaoObra.style.display = 'table-row';
        }
        renderMaoObraList(searchInputMaoObra.value);
        calcularCustoTotal();
    }
    
    // NOVO: Função para carregar os itens existentes do produto
    function carregarItensDoProduto() {
        if (!produtoData) return;

        // Carrega os insumos já associados
        if (produtoData.insumos && produtoData.insumos.length > 0) {
            produtoData.insumos.forEach(produtoInsumo => {
                // Supondo que o JSON tenha 'insumoId' e 'quantidadeUtilizada'
                adicionarInsumo(produtoInsumo.insumoId, produtoInsumo.quantidadeUtilizada);
            });
        }

        // Carrega as mãos de obra já associadas
        if (produtoData.maosObra && produtoData.maosObra.length > 0) {
            produtoData.maosObra.forEach(produtoMaoObra => {
                // Supondo que o JSON tenha 'maoObraId' e 'horasUtilizadas'
                adicionarMaoObra(produtoMaoObra.maoObraId, produtoMaoObra.horasUtilizadas);
            });
        }
    }

    // 7. EVENT LISTENERS
    searchInputInsumo.addEventListener('keyup', (e) => renderInsumoList(e.target.value));
    listaInsumosDisponiveis.addEventListener('click', (e) => {
        const btn = e.target.closest('.add-item-btn');
        if (btn) {
            adicionarInsumo(parseInt(btn.closest('.insumo-item').dataset.id, 10));
            calcularCustoTotal();
        }
    });
    itensInsumoBody.addEventListener('click', (e) => {
        const btn = e.target.closest('.remove-item-btn');
        if (btn) {
            removerInsumo(parseInt(btn.closest('tr').dataset.id, 10));
        }
    });
    itensInsumoBody.addEventListener('input', (e) => {
        if (e.target.classList.contains('item-input')) {
            calcularCustoTotal();
        }
    });

    searchInputMaoObra.addEventListener('keyup', (e) => renderMaoObraList(e.target.value));
    listaMaosObraDisponiveis.addEventListener('click', (e) => {
        const btn = e.target.closest('.add-item-btn');
        if (btn) {
            adicionarMaoObra(parseInt(btn.closest('.insumo-item').dataset.id, 10));
            calcularCustoTotal(); 
        }
    });
    itensMaoObraBody.addEventListener('click', (e) => {
        const btn = e.target.closest('.remove-item-btn');
        if (btn) {
            removerMaoObra(parseInt(btn.closest('tr').dataset.id, 10));
        }
    });
    itensMaoObraBody.addEventListener('input', (e) => {
        if (e.target.classList.contains('item-input')) {
            calcularCustoTotal();
        }
    });

    form.addEventListener('submit', function(e) {
        if (insumosAdicionados.size === 0 && maosObraAdicionadas.size === 0) {
            alert('Você precisa adicionar pelo menos um insumo ou uma mão de obra para cadastrar o produto.');
            e.preventDefault();
        }
    });

    // 8. INICIALIZAÇÃO
    renderInsumoList();
    renderMaoObraList();
    carregarItensDoProduto(); 
    calcularCustoTotal();    
});