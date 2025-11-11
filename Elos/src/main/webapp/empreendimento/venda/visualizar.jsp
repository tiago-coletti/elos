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
			<h2>Venda #${venda.id}</h2>
		</header>

		<section class="value-cards-container">
			<div class="value-card">
				<h3>Valor Total da Venda</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${venda.valorTotal}" type="currency" currencySymbol="R$ " />
				</p>
			</div>
			<div class="value-card">
				<h3>Data da Venda</h3>
				<p class="card-main-value">
					<fmt:parseDate value="${venda.dataVenda}" pattern="yyyy-MM-dd" var="parsedDataVenda" />
                    <fmt:formatDate value="${parsedDataVenda}" pattern="dd/MM/yyyy" />
				</p>
			</div>
		</section>

		<h3 class="table-section-title">Produtos Vendidos</h3>
		<div class="divTable">
			<table>		
				<thead>
					<tr>
						<th>Nome do Produto</th>
						<th>Quantidade</th>
						<th>Preço Unitário</th>
						<th>Subtotal</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty venda.produtos}">
							<c:forEach var="item" items="${venda.produtos}">
								<tr>
									<td><c:out value="${item.produtoNome}" /></td>	
									<td><c:out value="${item.quantidade}" /></td>
									<td>
										<fmt:formatNumber value="${item.precoUnitario}" type="currency" currencySymbol="R$ " />
									</td>
									<td>
										<fmt:formatNumber value="${item.quantidade * item.precoUnitario}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Nenhum produto associado a esta venda.</td>
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