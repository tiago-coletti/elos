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
        <h1>Listagem de Produtos</h1>
        <button id="new" onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/produto/incluir'">Incluir Produto</button>
    </div>

    <div class="search-wrapper">
        <div class="search-container">
            <i class='bx bx-search'></i>
            <input type="text" id="searchInput" placeholder="Pesquisar por nome do produto...">
        </div>
    </div>

    <div class="divTable">
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Data do registro</th>
                    <th>Preço de Venda</th>
                    <th>Visualizar</th>
                    <th>Editar</th>
                    <th>Excluir</th>
                </tr>
            </thead>
            <tbody id="produtosTable">
                <c:choose>
                    <c:when test="${not empty produtos}">
                        <c:forEach var="produto" items="${produtos}">
                            <fmt:parseDate value="${produto.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedCreatedAt" />
                            
                            <tr data-date="<fmt:formatDate value="${parsedCreatedAt}" pattern="yyyy-MM-dd" />">
                                <td><c:out value="${insumo.nome}"/></td>
                                <td>
                                    <fmt:formatDate value="${parsedCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$ " />
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/produto/visualizar" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${produto.id}' />">
                                        <button type="submit" class="action-icon view-icon" title="Visualizar">
                                            <i class='bx bxs-show'></i>
                                        </button>
                                    </form>
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/produto/editar" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${produto.id}' />">
                                        <button type="submit" class="action-icon edit-icon" title="Editar">
                                            <i class='bx bxs-edit'></i>
                                        </button>
                                    </form>
                                </td>
                                <td class="action-cell">
                                    <form action="${pageContext.request.contextPath}/empreendimento/produto/excluir" method="POST" style="display:inline;">
                                        <input type="hidden" name="id" value="<c:out value='${produto.id}' />">
                                        <button type="submit" class="action-icon delete-icon" title="Excluir" onclick="return confirm('Tem certeza que deseja excluir o produto?');">
                                            <i class='bx bxs-trash'></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr id="no-results">
                            <td colspan="6">Nenhum produto cadastrado</td>
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

            const tableBody = document.getElementById('produtosTable');
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