<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Insumo"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.gson.Gson"%>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Insumo> insumosDisponiveis = (ArrayList<Insumo>) request.getAttribute("insumosDisponiveis");

    String insumosJson = "[]";
    if (insumosDisponiveis != null) {
        insumosJson = new Gson().toJson(insumosDisponiveis);
    }
%>
	
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/formulario.css" />
</head>
  
<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	
	<div class="inclusao-container">
	
	    <a href="javascript:history.back()" class="back-button">
	        <i class='bx bx-arrow-back'></i>
	        <span>Voltar</span>
	    </a>
	    
	    <div class="inclusao-body">
	        <h2>Inclusão de Compra</h2>
	        
	        <form id="compraForm" action="${pageContext.request.contextPath}/empreendimento/compra/incluir" method="post">
	            
                <div class="form-group">
                    <label for="dataCompra">Data da Compra</label>
                    <input type="date" id="dataCompra" name="dataCompra" required>
                </div>

                <div class="compra-layout">
                    <!-- Coluna da Esquerda: Seleção de Insumos -->
                    <div class="selecao-insumos">
                        <h3>Insumos Disponíveis</h3>
                        <input type="text" id="searchInput" class="form-group input" placeholder="Pesquisar insumo...">
                        <div class="insumo-disponivel-list" id="insumoList">
                            <!-- Insumos serão populados pelo JavaScript -->
                        </div>
                    </div>

                    <!-- Coluna da Direita: Itens da Compra (Carrinho) -->
                    <div class="itens-compra-container">
                        <h3>Itens da Compra</h3>
                        <table class="itens-compra-table">
                            <thead>
                                <tr>
                                    <th>Insumo</th>
                                    <th>Quantidade</th>
                                    <th>Valor Unitário</th>
                                    <th>Subtotal</th>
                                    <th>Ação</th>
                                </tr>
                            </thead>
                            <tbody id="itensCompraBody">
                                <tr id="itens-compra-vazio">
                                    <td colspan="5">Nenhum item adicionado</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="compra-footer">
                    <div class="total-compra-container">
                        Valor Total: <span id="valorTotal">R$ 0,00</span>
                    </div>
                    <div class="form-actions">
                        <button type="submit">Cadastrar Compra</button>
                    </div>
                </div>
	        </form>
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script>
        // Passa a lista de insumos do JSP para o JavaScript
        const insumosDisponiveis = <%= insumosJson %>;

        document.addEventListener('DOMContentLoaded', function() {
            // Elementos do DOM
            const insumoListContainer = document.getElementById('insumoList');
            const itensCompraBody = document.getElementById('itensCompraBody');
            const searchInput = document.getElementById('searchInput');
            const valorTotalEl = document.getElementById('valorTotal');
            const form = document.getElementById('compraForm');
            const placeholderRow = document.getElementById('itens-compra-vazio');
            
            let itensAdicionados = new Set();

            // Função para renderizar a lista de insumos disponíveis
            function renderInsumoList(filter = '') {
                insumoListContainer.innerHTML = '';
                const filteredInsumos = insumosDisponiveis.filter(insumo => 
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

            // Função para adicionar um item ao carrinho
            function adicionarItem(insumoId) {
                if (itensAdicionados.has(insumoId)) return;

                const insumo = insumosDisponiveis.find(i => i.id === insumoId);
                if (!insumo) return;

                // Esconde a mensagem de "nenhum item"
                if (placeholderRow) placeholderRow.style.display = 'none';

                const newRow = document.createElement('tr');
                newRow.dataset.id = insumo.id;
                newRow.innerHTML = `
                    <td>
                        ${insumo.nome}
                        <input type="hidden" name="insumoId" value="${insumo.id}">
                    </td>
                    <td><input type="number" name="quantidade" class="item-input" min="0.01" step="0.01" required></td>
                    <td><input type="number" name="valorUnitario" class="item-input" min="0.01" step="0.01" required></td>
                    <td class="subtotal">R$ 0,00</td>
                    <td class="item-actions"><button type="button" class="remove-item-btn"> <i class='bx bxs-trash'></i> </button></td>
                `;

                itensCompraBody.appendChild(newRow);
                itensAdicionados.add(insumoId);
                renderInsumoList(searchInput.value); // Atualiza a lista para desabilitar o item
            }

            // Função para remover um item do carrinho
            function removerItem(insumoId) {
                itensAdicionados.delete(insumoId);
                const rowToRemove = itensCompraBody.querySelector(`tr[data-id='${insumoId}']`);
                if (rowToRemove) {
                    rowToRemove.remove();
                }
                
                // Mostra a mensagem de "nenhum item" se o carrinho ficar vazio
                if (itensAdicionados.size === 0 && placeholderRow) {
                    placeholderRow.style.display = 'table-row';
                }

                calcularTotal();
                renderInsumoList(searchInput.value); // Atualiza a lista para reabilitar o item
            }

            // Função para calcular totais
            function calcularTotal() {
                let totalGeral = 0;
                const rows = itensCompraBody.querySelectorAll('tr');

                rows.forEach(row => {
                    if (row.id === 'itens-compra-vazio') return;

                    const quantidade = parseFloat(row.querySelector('input[name="quantidade"]').value) || 0;
                    const valorUnitario = parseFloat(row.querySelector('input[name="valorUnitario"]').value) || 0;
                    const subtotal = quantidade * valorUnitario;
                    
                    row.querySelector('.subtotal').textContent = `R$ ${subtotal.toFixed(2)}`;
                    totalGeral += subtotal;
                });

                valorTotalEl.textContent = `R$ ${totalGeral.toFixed(2)}`;
            }

            // Event Listeners
            searchInput.addEventListener('keyup', () => renderInsumoList(searchInput.value));

            insumoListContainer.addEventListener('click', function(e) {
                if (e.target.classList.contains('add-item-btn')) {
                    const insumoId = parseInt(e.target.closest('.insumo-item').dataset.id, 10);
                    adicionarItem(insumoId);
                }
            });

            itensCompraBody.addEventListener('click', function(e) {
                if (e.target.closest('.remove-item-btn')) {
                    const insumoId = parseInt(e.target.closest('tr').dataset.id, 10);
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
                    e.preventDefault(); // Impede o envio do formulário
                }
            });

            // Define a data atual no campo de data
            document.getElementById('dataCompra').valueAsDate = new Date();

            // Renderização inicial
            renderInsumoList();
        });
    </script>
</body>
</html>
