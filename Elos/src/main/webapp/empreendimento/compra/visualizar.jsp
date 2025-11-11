<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos - Visualizar Compra</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/tabela.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/visualizar.css" />
</head>

<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

	<main class="product-view-container">
		
		<header class="product-header">
			<h2>Compra #${compra.id}</h2>
		</header>

		<section class="value-cards-container">
			<div class="value-card">
				<h3>Valor Total da Compra</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${compra.valorTotal}" type="currency" currencySymbol="R$ " />
				</p>
			</div>
			<div class="value-card">
				<h3>Data da Compra</h3>
				<p class="card-main-value">
					<fmt:parseDate value="${compra.dataCompra}" pattern="yyyy-MM-dd" var="parsedDataCompra" />
                    <fmt:formatDate value="${parsedDataCompra}" pattern="dd/MM/yyyy" />
				</p>
			</div>
		</section>

		<h3 class="table-section-title">Insumos Comprados</h3>
		<div class="divTable">
			<table>		
				<thead>
					<tr>
						<th>Nome do Insumo</th>
						<th>Quantidade Comprada</th>
						<th>Custo Unitário</th>
						<th>Subtotal</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty compra.insumos}">
							<c:forEach var="item" items="${compra.insumos}">
								<tr>
									<td><c:out value="${item.insumoNome}" /></td>	
									<td><c:out value="${item.quantidadeComprada}" /></td>
									<td>
										<fmt:formatNumber value="${item.precoUnitario}" type="currency" currencySymbol="R$ " />
									</td>
									<td>
										<fmt:formatNumber value="${item.quantidadeComprada * item.precoUnitario}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Nenhum insumo associado a esta compra.</td>
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