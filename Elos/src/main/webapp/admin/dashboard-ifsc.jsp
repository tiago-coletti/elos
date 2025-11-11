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
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/navbar.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/dashboard.css" />
</head>

<body>
    <%@ include file="/admin/shared/navbar.jspf"%>

    <main class="container-admin">
        <div class="dashboard-title">
            <h2>Visão Geral dos Empreendimentos (IFSC)</h2>
        </div>
        
        <section class="filter-bar">
            <form action="${pageContext.request.contextPath}/admin/dashboard-ifsc" method="GET" class="filter-form">
                <div class="filter-group">
                    <label for="semestre">Filtrar por Semestre</label>
                    <select name="semestre" id="semestre">
                        <option value="">Todos os Semestres</option>
                        <c:forEach var="semestre" items="${listaSemestres}">
                            <option value="${semestre}" ${semestre == filtroSemestre ? 'selected' : ''}>
                                <c:out value="${semestre}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="filter-group">
                    <label for="curso">Filtrar por Curso</label>
                    <select name="curso" id="curso">
                        <option value="">Todos os Cursos</option>
                        <option value="DESENVOLVIMENTO_SISTEMAS" ${'DESENVOLVIMENTO_SISTEMAS' == filtroCurso ? 'selected' : ''}>
                            Desenvolvimento de Sistemas
                        </option>
                        <option value="ENERGIA_RENOVAVEIS" ${'ENERGIA_RENOVAVEIS' == filtroCurso ? 'selected' : ''}>
                            Energias Renováveis
                        </option>
                    </select>
                </div>
                
                <div class="filter-actions">
                    <button type="submit" class="filter-button">Filtrar</button>
                    <a href="${pageContext.request.contextPath}/admin/dashboard-ifsc" class="filter-clear-button">Limpar</a>
                </div>
            </form>
        </section>

        <section class="card-grid-container">
            <c:choose>
                <c:when test="${not empty cardsData}">
                    <c:forEach var="card" items="${cardsData}">
                        <div class="admin-card">
                            <h3><c:out value="${card.nome}" /></h3>
                            <ul>
                                <li>
                                    <strong>Saldo:</strong>
                                    <span class="saldo">
                                        <fmt:formatNumber value="${card.saldo}" type="currency" currencySymbol="R$ " />
                                    </span>
                                </li>
                                <li>
                                    <strong>Insumos Cadastrados:</strong>
                                    <span><c:out value="${card.totalInsumos}" /></span>
                                </li>
                                <li>
                                    <strong>Compras Registradas:</strong>
                                    <span><c:out value="${card.totalCompras}" /></span>
                                </li>
                                <li>
                                    <strong>Vendas Registradas:</strong>
                                    <span><c:out value="${card.totalVendas}" /></span>
                                </li>
                            </ul>
                            
                            <a href="${pageContext.request.contextPath}/admin/abrir-empreendimento?id=${card.id}" class="admin-card-button">
                                <i class='bx bx-log-in-circle'></i> Abrir Empreendimento
                            </a>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="color: var(--grey-color);">Nenhum empreendimento (Aluno) encontrado com os filtros aplicados.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/admin/assets/js/navbar.js"></script>
</body>
</html>