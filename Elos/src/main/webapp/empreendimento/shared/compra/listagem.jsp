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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/listagem.css" />
</head>

<body>
    <%@ include file="/empreendimento/shared/navbar.jspf"%>

    <div class="page-header">
        <h1>Listagem de Compras</h1>
        <button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/compra/incluir'">Incluir Compra</button>
    </div>

    <div class="search-wrapper">
        <div class="date-filter-container">
            <div class="filter-group">
                <label for="startDateInput">Data Inicial:</label>
                <input type="date" id="startDateInput">
            </div>
            <div class="filter-group">
                <label for="endDateInput">Data Final:</label>
                <input type="date" id="endDateInput">
            </div>
            <button id="clearFilterBtn">Limpar</button>
        </div>
    </div>

    <div class="divTable">
        <table>
            <thead>
                <tr>
                    <th>Data da Compra</th>
                    <th>Data do registro</th>
                    <th>Valor Total</th>
                    <th>Visualizar</th>
                    <th>Editar</th>
                    <th>Excluir</th>
                </tr>
            </thead>
            <tbody id="comprasTable">
                <c:choose>
                    <c:when test="${not empty compras}">
                        <c:forEach var="compra" items="${compras}">
                            <fmt:parseDate value="${compra.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedCreatedAt" />
                            
                            <tr data-date="<fmt:formatDate value="${parsedCreatedAt}" pattern="yyyy-MM-dd" />">
                                <td>
                                    <fmt:parseDate value="${compra.dataCompra}" pattern="yyyy-MM-dd" var="parsedDataCompra" />
                                    <fmt:formatDate value="${parsedDataCompra}" pattern="dd/MM/yyyy" />
                                </td>
                                <td>
                                    <fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <fmt:formatNumber value="${compra.valorTotal}" type="currency" currencySymbol="R$ " />
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/compra/visualizar" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${compra.id}' />">
                                        <button type="submit" class="action-icon view-icon" title="Visualizar">
                                            <i class='bx bxs-show'></i>
                                        </button>
                                    </form>
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/compra/editar" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${compra.id}' />">
                                        <button type="submit" class="action-icon edit-icon" title="Editar">
                                            <i class='bx bxs-edit'></i>
                                        </button>
                                    </form>
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/compra/excluir" method="POST" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${compra.id}' />">
                                        <button type="submit" class="action-icon delete-icon" title="Excluir" onclick="return confirm('Tem certeza que deseja excluir a compra?');">
                                            <i class='bx bxs-trash'></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr id="no-results">
                            <td colspan="6">Nenhuma compra cadastrada</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            
            const startDateInput = document.getElementById('startDateInput');
            const endDateInput = document.getElementById('endDateInput');
            const clearFilterBtn = document.getElementById('clearFilterBtn');
            const tableBody = document.getElementById('comprasTable');
            const tableRows = tableBody.getElementsByTagName('tr');
            
            let noResultsRow;
            if (document.getElementById('no-results')) {
                noResultsRow = document.getElementById('no-results');
            } else {
                noResultsRow = document.createElement('tr');
                noResultsRow.id = 'no-results-js';
                noResultsRow.innerHTML = '<td colspan="6">Nenhum resultado encontrado para o filtro aplicado.</td>';
                tableBody.appendChild(noResultsRow);
            }
            noResultsRow.style.display = 'none';

            function applyFilter() {
                const startDate = startDateInput.value;
                const endDate = endDateInput.value;
                let visibleRows = 0;

                for (let i = 0; i < tableRows.length; i++) {
                    const row = tableRows[i];
                    if (row.id === 'no-results' || row.id === 'no-results-js') continue;

                    const rowDate = row.getAttribute('data-date');
                    let showRow = true;

                    if (startDate && rowDate < startDate) {
                        showRow = false;
                    }
                    
                    if (endDate) {
                        const finalDate = new Date(endDate + 'T00:00:00'); 
                        const linhaDate = new Date(rowDate + 'T00:00:00');

                        if (linhaDate > finalDate) {
                            showRow = false;
                        }
                    }

                    if (showRow) {
                        row.style.display = "";
                        visibleRows++;
                    } else {
                        row.style.display = "none";
                    }
                }

                if (visibleRows === 0 && (startDate || endDate)) {
                    noResultsRow.style.display = 'table-row';
                } else {
                    noResultsRow.style.display = 'none';
                }
            }

            function clearFilter() {
                startDateInput.value = '';
                endDateInput.value = '';
                applyFilter();
            }

            startDateInput.addEventListener('change', applyFilter);
            endDateInput.addEventListener('change', applyFilter);
            clearFilterBtn.addEventListener('click', clearFilter);
        });
    </script>
</body>
</html>