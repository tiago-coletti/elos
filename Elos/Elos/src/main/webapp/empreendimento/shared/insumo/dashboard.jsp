<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.entity.Insumo"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map" %>

<%
    @SuppressWarnings("unchecked")
    ArrayList<Insumo> insumos = (ArrayList<Insumo>) request.getAttribute("insumos");
    
    @SuppressWarnings("unchecked")
    Map<String, Object> dashboardData = (Map<String, Object>) request.getAttribute("dashboardData");
%>

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
            <h2>Painel de Insumos</h2>
        </div>

        <section class="carousel-container">
            <div class="carousel-wrapper">
                <div class="carousel-track">

                    <div class="card">
                        <h3>Insumos com Estoque Baixo</h3>
                        <p>Total de <span class="card-value"><c:out value="${dashboardData.insumosEstoqueBaixo.size()}" /></span> insumos abaixo do mínimo.</p>
                        <ul>
                            <c:forEach var="insumo" items="${dashboardData.insumosEstoqueBaixoCard}">
                                <li><strong><c:out value="${insumo.nome}" />:</strong> <c:out value="${insumo.quantidade}" /> <c:out value="${insumo.unidadeMedida}" /></li>
                            </c:forEach>
                        </ul>
                        <button class="view-details-btn action-button" data-modal-target="lowStockModal">Ver Lista Completa</button>
                    </div>

                    <div class="card">
                        <h3>Oportunidade de Troca Solidária <i class='bx bx-help-circle help-icon' data-modal-target="solidarityExchangeHelpModal"></i></h3>
                        <p>Insumos parados podem ser úteis para outros!</p>
                        <ul>
                            <li><strong>Itens sem giro:</strong> <c:out value="${dashboardData.insumosParados.size()}" /></li>
                            <li><strong>Potencial de troca:</strong> Libere espaço e recursos</li>
                        </ul>
                        <button class="view-details-btn" data-modal-target="stagnantStockModal">Ver Itens Parados</button>
                    </div>

                    <div class="card">
                        <h3>Valor Total do Estoque</h3>
                        <p class="card-value">
                            <fmt:setLocale value="pt_BR"/>
                            <fmt:formatNumber value="${dashboardData.valorTotalEstoque}" type="currency" currencySymbol="R$ " />
                        </p>
                        <p>Valor monetário total de todos os insumos.</p>
                    </div>
                    
                    <div class="card">
                        <h3>Últimos Insumos Adicionados</h3>
                        <p>Itens mais recentes cadastrados no sistema.</p>
                        <ul>
                            <c:forEach var="insumo" items="${dashboardData.ultimosInsumosAdicionados}">
                                <li><strong><c:out value="${insumo.nome}" />:</strong> <c:out value="${insumo.createdAt}" /></li>
                            </c:forEach>
                        </ul>
                    </div>

                </div>
            </div>
            <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>
        </section>
    </main>
    
    <div class="header">
        <button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/insumo/incluir.jsp'">Incluir</button>
    </div>
    
    <div class="divTable">
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Medida</th>
                    <th>Quantidade</th>
                </tr>
            </thead>
            <tbody id="insumosTable">
                <c:choose>
                    <c:when test="${not empty insumos}">
                        <c:forEach var="insumo" items="${insumos}" varStatus="status">
                            <c:if test="${status.count <= 3}">
                                <tr>
                                    <td><c:out value="${insumo.nome}" /></td>
                                    <td><c:out value="${insumo.unidadeMedida}" /></td>
                                    <td style="text-align: left;"><c:out value="${insumo.quantidade}" /></td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="3">Nenhum insumo cadastrado</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        <c:if test="${not empty insumos}">
            <a href="${pageContext.request.contextPath}/empreendimento/insumo/listagem" class="ver-todos-btn">Ver todos os insumos</a>
        </c:if>
    </div>
    
    <%@ include file="/empreendimento/shared/modals/insumo.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>