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
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/visualizar.css" />
</head>

<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

	<main class="product-view-container">
		
		<header class="product-header">
			<h2><c:out value="${produto.nome}"/></h2>
		</header>

		<section class="value-cards-container">
			<div class="value-card">
				<h3>Valor de Venda</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$ " />
				</p>
			</div>
			<div class="value-card">
				<h3>Custo de Produção (Insumos)</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${custoInsumos}" type="currency" currencySymbol="R$ " />
				</p>
			</div>
			<div class="value-card">
				<h3>Custo Total (Insumos + Mão de Obra)</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${custoTotal}" type="currency" currencySymbol="R$ " />
				</p>
			</div>
		</section>

		<h3 class="table-section-title">Insumos Associados</h3>
		<div class="divTable">
			<table>		
				<thead>
					<tr>
						<th>Nome do Insumo</th>
						<th>Quantidade</th>
						<th>Unidade de Medida</th>
						<th>Custo</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<%-- CORREÇÃO: Acessando a lista de insumos DENTRO do objeto produto --%>
						<c:when test="${not empty produto.insumos}">
							<c:forEach var="item" items="${produto.insumos}">
								<tr>
									<td><c:out value="${item.insumo.nome}" /></td>	
									<td><c:out value="${item.quantidadeUtilizada}" /></td>
									<td><c:out value="${item.insumo.unidadeMedida}" /></td>
									<td>
										<fmt:formatNumber value="${item.custoTotal}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Nenhum insumo associado a este produto.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
		
		<h3 class="table-section-title">Mão de Obra Associada</h3>
		<div class="divTable">
			<table>		
				<thead>
					<tr>
						<th>Etapa / Profissional</th>
						<th>Tempo Gasto (horas)</th>
						<th>Custo por Hora</th>
						<th>Custo Total da Etapa</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<%-- CORREÇÃO: Acessando a lista de mão de obra DENTRO do objeto produto --%>
						<c:when test="${not empty produto.maosObra}">
							<c:forEach var="item" items="${produto.maosObra}">
								<tr>
									<%-- CORREÇÃO: Usando 'nome' para a descrição da mão de obra --%>
									<td><c:out value="${item.maoObra.nome}" /></td>	
									<td><c:out value="${item.horasUtilizadas}" />h</td>
									<td>
										<%-- CORREÇÃO: Usando 'custoHora' para o valor da hora --%>
										<fmt:formatNumber value="${item.maoObra.custoHora}" type="currency" currencySymbol="R$ " />
									</td>
									<td>
										<fmt:formatNumber value="${item.custoTotalEtapa}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Nenhuma mão de obra associada a este produto.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</main>
    
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>