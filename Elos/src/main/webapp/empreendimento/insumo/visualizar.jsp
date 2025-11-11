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
			<h2><c:out value="${insumo.nome}"/></h2>
		</header>

		<section class="value-cards-container">
			<div class="value-card">
				<h3>Quantidade Total em Estoque</h3>
				<p class="card-main-value">
					<fmt:formatNumber value="${totalEstoque}" minFractionDigits="2" maxFractionDigits="3"/>
					<c:out value=" ${insumo.unidadeMedida}"/>
				</p>
			</div>
			<div class="value-card">
				<h3>Unidade de Medida</h3>
				<p class="card-main-value">
					<c:out value="${insumo.unidadeMedida}"/>
				</p>
			</div>
		</section>

		<h3 class="table-section-title">Lotes de Compra (Estoque)</h3>
		<div class="divTable">
			<table>		
				<thead>
					<tr>
						<th>Data da Compra</th>
						<th>Custo Unitário</th>
						<th>Quantidade Restante</th>
						<th>Ação</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty lotes}">
							<c:forEach var="lote" items="${lotes}">
								<tr>
									<td>
										<%-- Alterado de fmt:formatDate para c:out --%>
										<c:out value="${lote.dataCompra}" />
									</td>	
									<td>
										<fmt:formatNumber value="${lote.precoUnitario}" type="currency" currencySymbol="R$ " />
										/ <c:out value="${insumo.unidadeMedida}"/>
									</td>
									<td>
										<fmt:formatNumber value="${lote.quantidadeRestante}" minFractionDigits="2" maxFractionDigits="3"/>
										<c:out value=" ${insumo.unidadeMedida}"/>
									</td>
									<td>
										<button class="btn-primary" 
												data-lote-id="${lote.id}" 
												data-lote-max="${lote.quantidadeRestante}"
												onclick="abrirModalAjuste(this)">
											Ajustar Estoque
										</button>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Nenhum lote com estoque disponível para este insumo.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</main>
    
    <div id="modal-ajuste" class="modal-overlay">
		<div class="modal-content">
			<header class="modal-header">
				<h4>Ajuste de Estoque</h4>
				<span class="modal-close" onclick="fecharModalAjuste()">&times;</span>
			</header>
			
			<form id="form-ajuste" class="modal-form" action="${pageContext.request.contextPath}/empreendimento/insumo/ajustar-estoque" method="POST">
				<input type="hidden" name="compraInsumoId" id="modal-compra-insumo-id">
				<input type="hidden" name="insumoId" value="${insumo.id}">
				
				<div class="form-group">
					<label for="modal-quantidade">Quantidade a Baixar</label>
					<input type="number" id="modal-quantidade" name="quantidade" class="form-control" 
						   step="0.001" min="0.001" required>
					<small id="modal-max-label">Disponível neste lote: 0.00</small>
				</div>
				
				<div class="form-group">
					<label>Tipo de Ajuste</label>
					<div class="radio-group">
						<label>
							<input type="radio" name="tipoAjuste" value="perda" checked> Perda (Estragado, Vencido)
						</label>
						<label>
							<input type="radio" name="tipoAjuste" value="retirada"> Retirada (Uso Pessoal)
						</label>
					</div>
				</div>
				
				<footer class="modal-footer">
					<button type="button" class="btn-secondary" onclick="fecharModalAjuste()">Cancelar</button>
					<button type="submit" class="btn-primary">Confirmar Ajuste</button>
				</footer>
			</form>
		</div>
	</div>
    
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/visualizar.js"></script>
</body>
</html>