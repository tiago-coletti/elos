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
			<h2>Painel de Produtos</h2>
		</div>

		<section class="carousel-container">
			<div class="carousel-wrapper">
				<div class="carousel-track">

					<div class="card">
						<h3>Rentabilidade de Produtos</h3>
						<p>Produtos com maior margem de lucro.</p>
						<ul>
							<c:choose>
								<c:when test="${not empty dashboardData.produtosMaisRentaveisCard}">
									<c:forEach var="produto" items="${dashboardData.produtosMaisRentaveisCard}">
										<li>
											<strong><c:out value="${produto.nome}"/>:</strong>
											<fmt:formatNumber value="${produto.margem}" type="number" maxFractionDigits="0"/>% de margem
										</li>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<li>Nenhum produto para exibir.</li>
								</c:otherwise>
							</c:choose>
						</ul>
						<button class="view-details-btn" data-modal-target="topProfitProductsModal">Analisar Rentabilidade</button>
					</div>
					
					<div class="card">
						<h3>Saúde Financeira (Mês)</h3>
						<p>Análise de Custo e Receita dos produtos.</p>
						<ul>
							<li>Custo Total de Produção: <fmt:formatNumber value="${dashboardData.custoProducaoMes}" type="currency"/></li>
							<li>Receita Bruta: <fmt:formatNumber value="${dashboardData.receitaTotalMes}" type="currency"/></li>
						</ul>
						<button class="view-details-btn" data-modal-target="financialHealthModal">Ver Relatório Detalhado</button>
					</div>
					
					<div class="card">
						<h3>Variação de Vendas</h3>
						<p>Tendência vs. mês anterior.</p>
						<ul>
							<c:choose>
								<c:when test="${not empty dashboardData.variacaoVendas}">
									<c:forEach var="variacao" items="${dashboardData.variacaoVendas}" end="1">
			                            <li>
			                                <strong><c:out value="${variacao.nome}"/>:</strong> 
			                                <span style="color: ${variacao.variacao > 0 ? '#27ae60' : '#c0392b'};">
			                                    ${variacao.variacao > 0 ? '▲' : '▼'} <fmt:formatNumber value="${variacao.variacao}" type="number" maxFractionDigits="1"/>%
			                                </span>
			                            </li>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<li>Sem dados para comparar.</li>
								</c:otherwise>
							</c:choose>
						</ul>
						<button class="view-details-btn" data-modal-target="salesVariationModal">Ver Tendências</button>
					</div>

					<div class="card">
						<h3>Composição do Preço Justo</h3>
						<p>Análise da estrutura de custos.</p>
						<c:set var="produtoJusto" value="${dashboardData.produtoPrecoJusto}" />
						<ul>
							<c:choose>
								<c:when test="${not empty produtoJusto}">
									<c:set var="custoInsumos" value="${produtoJusto.getCustoTotalInsumos()}" />
									<c:set var="custoMaoObra" value="${produtoJusto.getCustoTotalMaoObra()}" />
									<c:set var="custoTotal" value="${custoInsumos + custoMaoObra}" />
									<li><strong>Custo Insumos (Médio):</strong> <fmt:formatNumber value="${(custoInsumos / produtoJusto.precoVenda) * 100}" type="number" maxFractionDigits="0"/>%</li>
									<li><strong>Valor Trabalho (Médio):</strong> <fmt:formatNumber value="${(custoMaoObra / produtoJusto.precoVenda) * 100}" type="number" maxFractionDigits="0"/>%</li>
								</c:when>
								<c:otherwise>
									<li>Nenhum produto para analisar.</li>
								</c:otherwise>
							</c:choose>
						</ul>
						<button class="view-details-btn action-button" data-modal-target="fairPriceModal">Analisar Estrutura</button>
					</div>

				</div>
			</div>
			<button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>
		</section>
	</main>
    
    <div class="header">
		<button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/produto/incluir'">Incluir</button>
	</div>
	
	<div class="divTable">
		<table>		
			<thead>
				<tr>
				    <th>Nome</th>
					<th>Data do registro</th>
					<th>Preço de Venda</th>
				</tr>
			</thead>
			
			<tbody id="comprasTable">
				<c:choose>
					<c:when test="${not empty produtos}">
						<c:forEach var="produto" items="${produtos}" end="2">
							<tr>
								<td><c:out value="${produto.nome}" /></td>	
								<td>
									<fmt:parseDate value="${produto.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedCreatedAt" />
									<fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy" />
								</td>
								<td>
									<fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$ " />
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="3">Nenhum produto cadastrado</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		
		<c:if test="${not empty produtos}">
			<a href="${pageContext.request.contextPath}/empreendimento/produto/listagem" class="ver-todos-btn">Ver todos os produtos</a>
		</c:if>
	</div>
    
    <%@ include file="/empreendimento/shared/modals/produto.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>