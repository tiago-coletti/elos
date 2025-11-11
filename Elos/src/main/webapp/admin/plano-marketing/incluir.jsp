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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/formulario.css" />
</head>

<body>
    <%@ include file="/admin/shared/navbar.jspf"%>

    <section class="inclusao-container">
        <a href="${pageContext.request.contextPath}/admin/plano-marketing/listagem" class="back-button">
            <i class='bx bx-chevron-left'></i>
            Voltar para a Listagem
        </a>

        <div class="inclusao-body">
            <h2>Adicionar Pergunta em: <c:out value="${param.qNome}" /></h2>
            
            <form action="${pageContext.request.contextPath}/admin/plano-marketing/incluir" method="POST">
                
                <input type="hidden" name="questionarioId" value="${param.qId}" />

                <div class="form-group">
                    <label for="textoPergunta">Texto da Pergunta</label>
                    <textarea id="textoPergunta" name="textoPergunta" rows="4" required class="form-control"></textarea>
                </div>
                
                <div class="form-group">
                    <label for="tipoPergunta">Tipo de Resposta</label>
                    <select id="tipoPergunta" name="tipoPergunta" required class="form-control">
                        <option value="TEXTO_CURTO">Texto Curto</option>
                        <option value="TEXTO_LONGO">Texto Longo</option>
                        <option value="NUMERO">Número</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="ordem">Ordem de Exibição</label>
                    <input type="number" id="ordem" name="ordem" value="0" required class="form-control" />
                </div>
                
                <div class="form-actions">
                    <button type="submit">Salvar Pergunta</button>
                </div>
            </form>
        </div>
    </section>

    <script src="${pageContext.request.contextPath}/admin/assets/js/navbar.js"></script>
</body>
</html>