<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

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
        <h1>Listagem de Insumos</h1>
        <button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/insumo/incluir.jsp'">Incluir</button>
    </div>

    <div class="search-wrapper">
        <div class="search-container">
            <i class='bx bx-search'></i>
            <input type="text" id="searchInput" placeholder="Pesquisar por nome do insumo...">
        </div>
    </div>

    <div class="divTable">
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Medida</th>
                    <th>Quantidade</th>
                    <th>Editar</th>
                    <th>Excluir</th>
                </tr>
            </thead>
            <tbody id="insumosTable">
                <c:choose>
                    <c:when test="${not empty insumos}">
                        <c:forEach var="insumo" items="${insumos}">
                            <tr>
                                <td><c:out value="${insumo.nome}" /></td>
                                <td><c:out value="${insumo.unidadeMedida}" /></td>
                                <td><c:out value="${insumo.quantidade}" /></td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/insumo/editar" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${insumo.id}' />">
                                        <button type="submit" class="action-icon edit-icon" title="Editar">
                                            <i class='bx bxs-edit'></i>
                                        </button>
                                    </form>
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/insumo/excluir" method="POST" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${insumo.id}' />">
                                        <button type="submit" class="action-icon delete-icon" title="Excluir" onclick="return confirm('Tem certeza que deseja excluir o insumo <c:out value="${insumo.nome}" />?');">
                                            <i class='bx bxs-trash'></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr id="no-results">
                            <td colspan="5">Nenhum insumo cadastrado</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const searchInput = document.getElementById('searchInput');
            if (!searchInput) return;

            const tableBody = document.getElementById('insumosTable');
            const tableRows = tableBody.getElementsByTagName('tr');
            const noResultsRow = document.getElementById('no-results');

            if (noResultsRow && tableRows.length === 1) {
                searchInput.disabled = true;
                return;
            } else if (noResultsRow) {
                noResultsRow.style.display = 'none';
            }

            searchInput.addEventListener('keyup', function () {
                const filter = searchInput.value.toLowerCase();
                let visibleRows = 0;

                for (let i = 0; i < tableRows.length; i++) {
                    if (tableRows[i].id === 'no-results') continue;
                    
                    const nameTd = tableRows[i].getElementsByTagName('td')[0];
                    if (nameTd) {
                        const nameText = nameTd.textContent || nameTd.innerText;
                        if (nameText.toLowerCase().indexOf(filter) > -1) {
                            tableRows[i].style.display = "";
                            visibleRows++;
                        } else {
                            tableRows[i].style.display = "none";
                        }
                    }
                }
                
                if (noResultsRow) {
                    if (visibleRows === 0) {
                        noResultsRow.style.display = 'table-row';
                        noResultsRow.getElementsByTagName('td')[0].textContent = 'Nenhum resultado encontrado';
                    } else {
                        noResultsRow.style.display = 'none';
                    }
                }
            });
        });
    </script>
</body>
</html>