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
	        <h2>Painel de Mãos de Obra</h2>
	    </div>
	
	    <section class="carousel-container">
	    
	        <div class="carousel-wrapper">
	            <div class="carousel-track">
	
	                <div class="card">
	                    <h3>Mãos de Obra Mais Usadas</h3>
	                    <p>Mãos de obra que mais contribuíram em horas para os produtos.</p>
	                    <ul>
	                    	<c:choose>
	                    		<c:when test="${not empty dashboardData.maisUtilizadas}">
			                    	<c:forEach var="item" items="${dashboardData.maisUtilizadas}">
				                        <li><strong><c:out value="${item.nome}"/>:</strong> <fmt:formatNumber value="${item.total_horas}" maxFractionDigits="1" /> horas</li>
			                    	</c:forEach>
	                    		</c:when>
	                    		<c:otherwise>
	                    			<li>Nenhuma hora registrada.</li>
	                    		</c:otherwise>
	                    	</c:choose>
	                    </ul>
	                    <button class="view-details-btn action-button" data-modal-target="mostUsedLaborModal">Ver Detalhes</button>
	                </div>

	                <div class="card">
	                    <h3>Mão de Obra de Maior Valor/Hora</h3>
	                    <p>Lista de trabalhos com o valor mais alto por hora.</p>
	                    <ul>
	                    	<c:choose>
	                    		<c:when test="${not empty dashboardData.maisCaras}">
			                        <c:forEach var="item" items="${dashboardData.maisCaras}">
				                        <li><strong><c:out value="${item.nome}"/>:</strong> <fmt:formatNumber value="${item.custoHora}" type="currency" />/h</li>
			                    	</c:forEach>
	                    		</c:when>
	                    		<c:otherwise>
									<li>Nenhuma mão de obra cadastrada.</li>
	                    		</c:otherwise>
	                    	</c:choose>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="highValueLaborModal">Ver Detalhes</button>
	                </div>

	                <div class="card">
	                    <h3>Últimas Mãos de Obra Cadastradas</h3>
	                    <p>Registros mais recentes adicionados ao sistema.</p>
	                    <ul>
	                    	<c:choose>
	                    		<c:when test="${not empty dashboardData.ultimasCadastradas}">
			                        <c:forEach var="item" items="${dashboardData.ultimasCadastradas}" end="2">
			                        	<li><strong><c:out value="${item.nome}"/>:</strong> <c:out value="${item.createdAt}"/></li>
			                        </c:forEach>
	                    		</c:when>
	                    		<c:otherwise>
	                    			<li>Nenhum cadastro recente.</li>
	                    		</c:otherwise>
	                    	</c:choose>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="recentLaborModal">Ver Mais</button>
	                </div>
	
	            </div>
	        </div>
	        <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>     
	            
	    </section>
	</main>
    
    <div class="header">
		<button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/mao-obra/incluir.jsp'">Incluir</button>
	</div>
	
	<div class="divTable">
		<table>		
			<thead>
				<tr>
					<th>Nome</th>
					<th>Custo por Hora</th>
				</tr>
			</thead>
			
			<tbody id="maosObraTable">
				<c:choose>
					<c:when test="${not empty maosObra}">
						<c:forEach var="maoObra" items="${maosObra}" varStatus="status">
							<c:if test="${status.count <= 3}">
								<tr>
									<td><c:out value="${maoObra.nome}" /></td>
									<td style="text-align: left;">
										<fmt:formatNumber value="${maoObra.custoHora}" type="currency" currencySymbol="R$ " />
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="2">Nenhuma mão de obra cadastrada</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		
		<c:if test="${not empty maosObra}">
			<a href="${pageContext.request.contextPath}/empreendimento/mao-obra/listagem" class="ver-todos-btn">Ver todas as mãos de obra</a>
		</c:if>

	</div>
    
    <%@ include file="/empreendimento/shared/modals/mao-obra.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>