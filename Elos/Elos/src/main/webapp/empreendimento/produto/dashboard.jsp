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
						<h3>Receita com Vendas</h3>
						<p>Período: <select><option>Este Mês</option><option>Últimos 3 meses</option><option>Ano</option></select></p>
						<p class="card-value">R$ 7.350,00</p>
						<p>Valor total arrecadado com a venda dos produtos.</p>
					</div>

					<div class="card">
						<h3>Produtos Mais Vendidos</h3>
						<p>Os preferidos da sua comunidade.</p>
						<ul>
							<li><strong>Bolo de Cenoura:</strong> 45 unidades</li>
							<li><strong>Pão Artesanal:</strong> 38 unidades</li>
						</ul>
						<button class="view-details-btn" data-modal-target="topSellingProductsModal">Ver Ranking Completo</button>
					</div>

					<div class="card">
						<h3>Impacto e Transparência <i class='bx bx-help-circle help-icon' data-modal-target="socialImpactHelpModal"></i></h3>
						<p>Mostre o valor do seu trabalho.</p>
						<ul>
							<li><strong>Insumos Locais:</strong> 75%</li>
							<li><strong>Reinvestido na Comunidade:</strong> R$ 735,00</li>
						</ul>
						<button class="view-details-btn" data-modal-target="socialImpactModal">Saber Mais</button>
					</div>

					<div class="card">
						<h3>Sugestões de Produção</h3>
						<p>Produtos que precisam ser repostos.</p>
						<ul>
							<li><strong>Estoque Baixo:</strong> 3 produtos</li>
							<li><strong>Itens Críticos:</strong> 1 produto zerado</li>
						</ul>
						<button class="view-details-btn action-button" data-modal-target="productionSuggestionModal">Planejar Produção</button>
					</div>

					<div class="card">
						<h3>Saúde Financeira (Mês)</h3>
						<p>Análise de Custo e Receita dos produtos.</p>
						<ul>
							<li><strong>Custo Total de Produção:</strong> R$ 4.850,00</li>
							<li><strong>Receita Bruta:</strong> R$ 7.350,00</li>
						</ul>
						<button class="view-details-btn" data-modal-target="financialHealthModal">Ver Relatório Detalhado</button>
					</div>
					
					<div class="card">
						<h3>Voz da Comunidade</h3>
						<p>Feedbacks recentes dos seus clientes.</p>
						<ul>
							<li><strong>Avaliação Média:</strong> 4.8 <i class='bx bxs-star' style='color:#f1c40f'></i></li>
							<li><strong>Novos Comentários:</strong> 3</li>
						</ul>
						<button class="view-details-btn" data-modal-target="customerFeedbackModal">Ler Avaliações</button>
					</div>

					<div class="card">
						<h3>Estoque de Produtos</h3>
						<p>Visão geral dos produtos prontos para venda.</p>
						<ul>
							<li><strong>Valor em Estoque:</strong> R$ 2.150,00</li>
							<li><strong>Produtos distintos:</strong> 12 tipos</li>
						</ul>
						<button class="view-details-btn" data-modal-target="productStockModal">Detalhar Estoque</button>
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
						<c:forEach var="compra" items="${produtos}" varStatus="status">
							<c:if test="${status.count <= 3}">
								<tr>
									<td><c:out value="${produto.nome}" /></td>	
									<td>
										<fmt:parseDate value="${produto.createdAt}" pattern="yyyy-MM-dd" var="parsedCreatedAt" />
										<fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy" />
									</td>
									<td>
										<fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:if>
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