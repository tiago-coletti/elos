<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

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
	                        <li><strong>Compra #102:</strong> Valor Total R$ 350,00</li>
	                        <li><strong>Compra #101:</strong> Valor Total R$ 1.200,00</li>
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
	                        <li><strong>Itens Críticos:</strong> 1 item zerado</li>
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
	                    <h3>Origem dos Insumos</h3>
	                    <p>Distribuição das compras por tipo de produtor.</p>
	                     <ul>
	                        <li><strong>Cooperativas/Locais:</strong> 55%</li>
	                        <li><strong>Grandes Atacadistas:</strong> 45%</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="insumoOriginModal">Ver Análise</button>
	                </div>
	
	                <div class="card">
	                    <h3>Nível de Estoque Atual</h3>
	                    <p>Visão geral dos insumos armazenados.</p>
	                    <ul>
	                        <li><strong>Valor em Estoque:</strong> R$ 3.120,00</li>
	                        <li><strong>Itens distintos:</strong> 28 tipos</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="stockLevelModal">Detalhar Estoque</button>
	                </div>
	
	            </div>
	        </div>
	        <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>     
            
	    </section>
	</main>
    
    <div class="header">
		<button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/compra/incluir'">Incluir</button>
	</div>
	
	<div class="divTable">
		<table>		
			<thead>
				<tr>
					<th>Data da Compra</th>
					<th>Data do registro</th>
					<th>Valor Total</th>
				</tr>
			</thead>
			
			<tbody id="comprasTable">
				<c:choose>
					<c:when test="${not empty compras}">
						<c:forEach var="compra" items="${compras}" varStatus="status">
							<c:if test="${status.count <= 3}">
								<tr>
									<td>
										<fmt:parseDate value="${compra.dataCompra}" pattern="yyyy-MM-dd" var="parsedDataCompra" />
										<fmt:formatDate value="${parsedDataCompra}" pattern="dd/MM/yyyy" />
									</td>
									<td>
										<fmt:parseDate value="${compra.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedCreatedAt" />
										<fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
									</td>
									<td>
										<fmt:formatNumber value="${compra.valorTotal}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="3">Nenhuma compra cadastrada</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		
		<c:if test="${not empty compras}">
			<a href="${pageContext.request.contextPath}/empreendimento/compra/listagem" class="ver-todos-btn">Ver todas as compras</a>
		</c:if>
	</div>
    
    <%@ include file="/empreendimento/shared/modals/compra.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>