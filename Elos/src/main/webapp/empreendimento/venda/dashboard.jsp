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
	        <h2>Painel de Vendas</h2>
	    </div>
	
	    <section class="carousel-container">
	        <div class="carousel-wrapper">
	            <div class="carousel-track">
	
	                <div class="card">
	                    <h3>Total Vendido (Este Mês)</h3>
	                    <p>Faturamento total das vendas realizadas no mês corrente.</p>
	                    <p class="card-value"><fmt:formatNumber value="${dashboardData.totalVendidoMes}" type="currency" /></p>
	                    <button class="view-details-btn" data-modal-target="totalSalesModal">Ver Anual</button>
	                </div>
	
	                <div class="card">
	                    <h3>Vendas Realizadas (Este Mês)</h3>
	                    <p>Número total de pedidos e vendas registradas no mês.</p>
	                    <p class="card-value"><c:out value="${dashboardData.contagemVendasMes}" /> Vendas</p>
	                    <button class="view-details-btn" data-modal-target="salesCountModal">Ver Anual</button>
	                </div>
	
	                <div class="card">
	                    <h3>Últimas Vendas Realizadas</h3>
	                    <p>Acompanhe as saídas mais recentes.</p>
	                    <ul>
	                    	<c:forEach var="venda" items="${dashboardData.ultimasVendas}">
		                        <li><strong>Venda #${venda.id}:</strong> Valor Total <fmt:formatNumber value="${venda.valorTotal}" type="currency" /></li>
	                    	</c:forEach>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="recentSalesModal">Ver Detalhes</button>
	                </div>
	
	                <div class="card">
	                    <h3>Produtos Mais Vendidos (Mês)</h3>
	                    <p>Itens que mais geraram faturamento no período.</p>
	                    <ul>
	                        <c:forEach var="produto" items="${dashboardData.produtosMaisVendidos}">
		                        <li><strong><c:out value="${produto.nome}"/>:</strong> <fmt:formatNumber value="${produto.total}" type="currency" /></li>
	                    	</c:forEach>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="topProductsModal">Ver Relatório Completo</button>
	                </div>
	
	            </div>
	        </div>
	        <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>     
            
	    </section>
	</main>
    
    <div class="header">
		<button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/venda/incluir'">Incluir Venda</button>
	</div>
	
	<div class="divTable">
		<table>		
			<thead>
				<tr>
					<th>Data da Venda</th>
					<th>Data do registro</th>
					<th>Valor Total</th>
				</tr>
			</thead>
			
			<tbody id="vendasTable">
				<c:choose>
					<c:when test="${not empty vendas}">
						<c:forEach var="venda" items="${vendas}" varStatus="status">
							<c:if test="${status.count <= 3}">
								<tr>
									<td>
										<fmt:parseDate value="${venda.dataVenda}" pattern="yyyy-MM-dd" var="parsedDataVenda" />
										<fmt:formatDate value="${parsedDataVenda}" pattern="dd/MM/yyyy" />
									</td>
									<td>
										<fmt:parseDate value="${venda.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedCreatedAt" />
										<fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
									</td>
									<td>
										<fmt:formatNumber value="${venda.valorTotal}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="3">Nenhuma venda cadastrada</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		
		<c:if test="${not empty vendas}">
			<a href="${pageContext.request.contextPath}/empreendimento/venda/listagem" class="ver-todos-btn">Ver todas as vendas</a>
		</c:if>
	</div>
    
    <%@ include file="/empreendimento/shared/modals/venda.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>