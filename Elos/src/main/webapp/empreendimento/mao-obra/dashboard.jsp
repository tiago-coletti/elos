<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.MaoObra"%>
<%@ page import="java.util.ArrayList"%>
<% @SuppressWarnings("unchecked") ArrayList<MaoObra> maosObra = (ArrayList<MaoObra>) request.getAttribute("maosObra"); %>

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
	                        <li><strong>Criação de Artesanato:</strong> 150 horas</li>
	                        <li><strong>Colheita e Preparo:</strong> 120 horas</li>
	                    </ul>
	                    <button class="view-details-btn action-button" data-modal-target="mostUsedLaborModal">Ver Detalhes</button>
	                </div>
	
	                <div class="card">
	                    <h3>Mão de Obra de Maior Valor/Hora</h3>
	                    <p>Lista de trabalhos com o valor mais alto por hora.</p>
	                    <ul>
	                        <li><strong>Design Gráfico:</strong> R$ 45,00/h</li>
	                        <li><strong>Consultoria Financeira:</strong> R$ 60,00/h</li>
	                    </ul>
	                    <button class="view-details-btn" data-modal-target="highValueLaborModal">Ver Detalhes</button>
	                </div>
	
	                <div class="card">
	                    <h3>Valor Total de Mão de Obra (Último Mês)</h3>
	                    <p class="card-value">R$ 5.850,00</p>
	                    <p>Total monetário de horas de trabalho no período.</p>
	                </div>
	
	                <div class="card">
	                    <h3>Mãos de Obra com Mais Horas Registradas</h3>
	                    <p>Período: <select><option>Mês</option><option>Semana</option><option>Trimestre</option></select></p>
	                    <ul>
	                        <li><strong>Colheita e Preparo:</strong> 250 horas</li>
	                        <li><strong>Venda Direta:</strong> 180 horas</li>
	                    </ul>
	                </div>
	
	                <div class="card">
	                    <h3>Últimas Mãos de Obra Cadastradas</h3>
	                    <ul>
	                        <li><strong>Embalagem de Doces:</strong> 2025-07-20</li>
	                        <li><strong>Entrega de Produtos:</strong> 2025-07-18</li>
	                    </ul>
	                </div>
	
	                <div class="card">
	                    <h3>Oportunidades de Mão de Obra <i class='bx bx-help-circle help-icon' data-modal-target="opportunityHelpModal"></i></h3>
	                    <p>15 horas de trabalho não associadas a produtos.</p>
	                    <button class="view-details-btn" data-modal-target="opportunityModal">Ver Detalhes</button>
	                </div>
	
	                <div class="card">
	                    <h3>Produtividade por Mão de Obra <i class='bx bx-help-circle help-icon' data-modal-target="productivityHelpModal"></i></h3>
	                    <p><strong>Criação de Artesanato:</strong> ~3 peças/h</p>
	                    <p><strong>Colheita e Preparo:</strong> ~2 pães/h</p>
	                    <button class="view-details-btn" data-modal-target="productivityModal">Ver Detalhes</button>
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
			<% 
			int count = 0;
			if (maosObra != null && !maosObra.isEmpty()) { 
				for (MaoObra maoObra : maosObra) { 
					if(count >= 3) break;
			%>
					<tr>
						<td><%=maoObra.getNome()%></td>
						<td style="text-align: left;">R$ <%=maoObra.getCustoHora()%></td>
					</tr>
			<% count++; 
			
			}} else { %>
				<tr>
					<td colspan="3">Nenhuma mão de obra cadastrada</td>
				</tr>
			<% } %>
							
			</tbody>
		</table>
		
		<% if (maosObra != null && !maosObra.isEmpty()) { %>
			<a href="${pageContext.request.contextPath}/empreendimento/mao-obra/listagem" class="ver-todos-btn">Ver todas as mãos de obra</a>
		<% } %>

	</div>
    
    <%@ include file="/empreendimento/shared/modals/mao-obra.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>