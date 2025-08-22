<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Compra"%>
<%@ page import="java.util.ArrayList"%>
<% @SuppressWarnings("unchecked") ArrayList<Compra> compras = (ArrayList<Compra>) request.getAttribute("compras"); %>

<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/tabela.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/carrossel.css" />
</head>

<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

    <main class="container">
	    <div class="dashboard-title">
	        <h2>Painel de Compras</h2>
	    </div>
	
	    <section class="carousel-container">
    
	        <div class="carousel-wrapper">
	            <div class="carousel-track">
	
	                <div class="card">
	                    <h3>Total Gasto em Compras</h3>
	                    <p>Período: <select><option>Este Mês</option><option>Últimos 3 meses</option><option>Ano</option></select></p>
	                    <p class="card-value">R$ 4.850,00</p>
	                    <p>Valor total investido na aquisição de insumos.</p>
	                </div>
	
	                <div class="card">
	                    <h3>Últimas Compras Realizadas</h3>
	                    <p>Acompanhe as entradas de insumo mais recentes.</p>
	                    <ul>
	                        <li><strong>Compra #102:</strong> Fornecedor A (R$ 350,00)</li>
	                        <li><strong>Compra #101:</strong> Cooperativa Local (R$ 1.200,00)</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="recentPurchasesModal">Ver Detalhes</button>
	                </div>
	
	                <div class="card">
	                    <h3>Variação de Preços <i class='bx bx-help-circle help-icon' data-modal-target="priceVariationHelpModal"></i></h3>
	                    <p>Itens com maiores alterações de custo.</p>
	                    <ul>
	                        <li><strong>Farinha de Trigo:</strong> <span style="color: #c0392b;">▲ 8%</span> na última compra</li>
	                        <li><strong>Embalagem Pote:</strong> <span style="color: #27ae60;">▼ 5%</span> na última compra</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="priceVariationModal">Analisar Histórico</button>
	                </div>
	
	                <div class="card">
	                    <h3>Sugestões de Compra</h3>
	                    <p>Itens que precisam de reposição urgente.</p>
	                    <ul>
	                        <li><strong>Estoque Baixo:</strong> 4 itens</li>
	                        <li><strong>Perto do Vencimento:</strong> 2 itens</li>
	                    </ul>
	                    <button class="view-details-btn action-button" data-modal-target="purchaseSuggestionModal">Ver Lista de Compras</button>
	                </div>
	
	                <div class="card">
	                    <h3>Maiores Despesas (Mês)</h3>
	                    <p>Insumos que mais consumiram o orçamento de compras.</p>
	                    <ul>
	                        <li><strong>Chocolate em Barra:</strong> R$ 950,00</li>
	                        <li><strong>Leite Condensado:</strong> R$ 720,00</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="topExpensesModal">Ver Relatório Completo</button>
	                </div>
	                
	                <div class="card">
	                    <h3>Principais Fornecedores</h3>
	                    <p>Parceiros com maior volume de compras no mês.</p>
	                    <ul>
	                        <li><strong>Atacado Central:</strong> R$ 2.150,00</li>
	                        <li><strong>Cooperativa Agrícola:</strong> R$ 1.800,00</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="topSuppliersModal">Ver Todos</button>
	                </div>
	
	                <div class="card">
	                    <h3>Contas a Pagar (Compras)</h3>
	                    <p>Faturas de compras com vencimento próximo.</p>
	                    <ul>
	                        <li><strong>Próximos 7 dias:</strong> R$ 890,00</li>
	                        <li><strong>Próximos 30 dias:</strong> R$ 2.450,00</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="accountsPayableModal">Gerenciar Faturas</button>
	                </div>
	
	            </div>
	        </div>
	        <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>     
            
	    </section>
	</main>
    
    <div class="header">
		<button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/compra/incluir.jsp'">Incluir</button>
	</div>
	
	<div class="divTable">
		<table>		
			<thead>
				<tr>
					<th>Valor Total</th>
					<th>Data</th>
				</tr>
			</thead>
			
			<tbody id="comprasTable">
			<% 
			int count = 0;
			if (compras != null && !compras.isEmpty()) { 
				for (Compra compra : compras) { 
					if(count >= 3) break;
			%>
					<tr>
						<td><%=compra.getValorTotal()%></td>
						<td><%=compra.getCreatedAt()%></td>
					</tr>
			<% count++; 
			
			}} else { %>
				<tr>
					<td colspan="3">Nenhuma compra cadastrada</td>
				</tr>
			<% } %>
							
			</tbody>
		</table>
		
		<% if (compras != null && !compras.isEmpty()) { %>
			<a href="${pageContext.request.contextPath}/empreendimento/compra/listagem" class="ver-todos-btn">Ver todas as compras</a>
		<% } %>

	</div>
    
    <%@ include file="/empreendimento/shared/modals/compra.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>