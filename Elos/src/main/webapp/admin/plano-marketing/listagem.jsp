<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
    <title>Elos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/navbar.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/listagem.css" />
    <style>
        .action-cell form { display: inline-block; margin: 0; padding: 0; }
        .action-cell .action-icon { background: none; border: none; padding: 0; }
        .action-cell .delete-icon { color: var(--red-color); }
        .action-cell .delete-icon:hover { color: #a00; }
    </style>
</head>

<body>
    <%@ include file="/admin/shared/navbar.jspf"%>

    <header class="page-header">
        <h1>Gerenciar Perguntas: <c:out value="${questionario.nome}" /></h1>
        
        <c:url var="urlIncluir" value="/admin/plano-marketing/incluir">
            <c:param name="qId" value="${questionario.id}" />
            <c:param name="qNome" value="${questionario.nome}" />
        </c:url>
        
        <button id="new" onclick="window.location.href='${urlIncluir}'">
            <i class='bx bx-plus'></i> Adicionar Pergunta
        </button>
    </header>

    <div class="divTable">
        <table>		
            <thead>
                <tr>
                    <th style="width: 80px;">Ordem</th>
                    <th>Texto da Pergunta</th>
                    <th style="width: 150px;">Tipo</th>
                    <th class="action-cell" style="width: 100px;">Ações</th>
                </tr>
            </thead>
            
            <tbody id="perguntasTable">
                <c:choose>
                    <c:when test="${not empty questionario.perguntas}">
                        <c:forEach var="pergunta" items="${questionario.perguntas}">
                            <tr>
                                <td><c:out value="${pergunta.ordem}" /></td>
                                <td><c:out value="${pergunta.textoPergunta}" /></td>	
                                <td><c:out value="${pergunta.tipoPergunta}" /></td>
                                <td class="action-cell">
                                    <c:url var="urlEditar" value="/admin/plano-marketing/editar">
                                        <c:param name="id" value="${pergunta.id}" />
                                    </c:url>
                                    <a href="${urlEditar}" class="action-icon edit-icon" title="Editar">
                                        <i class='bx bxs-edit'></i>
                                    </a>
                                    
                                    <form action="${pageContext.request.contextPath}/admin/plano-marketing/excluir" method="POST" style="display: inline;">
                                        <input type="hidden" name="perguntaId" value="${pergunta.id}" />
                                        <button type="submit" class="action-icon delete-icon" title="Excluir" onclick="return confirm('Tem certeza que deseja excluir esta pergunta?');">
                                            <i class='bx bxs-trash'></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr id="no-results">
                            <td colspan="4">Nenhuma pergunta cadastrada para este questionário.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <script src="${pageContext.request.contextPath}/admin/assets/js/navbar.js"></script>
</body>
</html>