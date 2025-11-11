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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/formulario.css" />
    
    <style>
        .form-actions {
            display: flex; /* Alinha botões lado a lado */
            justify-content: flex-end; /* Alinha à direita */
            gap: 15px; /* espaço entre botões */
        }
        /* Ajuste para botão de PDF secundário */
        .btn-secondary {
            background-color: transparent;
            color: var(--blue-color);
            border: 1px solid var(--blue-color);
        }
        .btn-secondary:hover {
            background-color: #f0f0f0;
        }
         body.dark .btn-secondary {
            color: var(--white-color);
            border-color: var(--white-color);
            background-color: transparent;
         }
         body.dark .btn-secondary:hover {
            background-color: #444;
         }
    </style>
</head>

<body>
    <%@ include file="/empreendimento/shared/navbar.jspf"%>

    <section class="inclusao-container">
        
        <div class="inclusao-body">
            <h2><c:out value="${questionario.nome}" /></h2>
            <p style="margin-bottom: 30px; color: var(--grey-color);">
                Responda às perguntas abaixo para construir seu plano. Suas respostas são salvas automaticamente.
            </p>
            
            <form id="form-questionario" action="${pageContext.request.contextPath}/empreendimento/plano-marketing" method="POST">
                
                <c:choose>
                    <c:when test="${not empty questionario.perguntas}">
                        <c:forEach var="pergunta" items="${questionario.perguntas}">
                            <div class="form-group">
                                <label for="resposta_${pergunta.id}">
                                    <c:out value="${pergunta.ordem}" />. <c:out value="${pergunta.textoPergunta}" />
                                </label>
                                
                                <c:set var="respostaSalva" value="${mapaRespostas[pergunta.id]}" />
                                
                                <c:choose>
                                    <c:when test="${pergunta.tipoPergunta == 'TEXTO_LONGO'}">
                                        <textarea id="resposta_${pergunta.id}" 
                                                  name="resposta_${pergunta.id}" 
                                                  rows="5" 
                                                  class="form-control"><c:out value="${respostaSalva}" /></textarea>
                                    </c:when>
                                    
                                    <c:when test="${pergunta.tipoPergunta == 'NUMERO'}">
                                        <input type="number" 
                                               id="resposta_${pergunta.id}" 
                                               name="resposta_${pergunta.id}" 
                                               value="<c:out value="${respostaSalva}" />" 
                                               class="form-control" />
                                    </c:when>
                                    
                                    <c:otherwise>
                                        <input type="text" 
                                               id="resposta_${pergunta.id}" 
                                               name="resposta_${pergunta.id}" 
                                               value="<c:out value="${respostaSalva}" />" 
                                               class="form-control" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p style="color: var(--grey-color);">Nenhuma pergunta foi cadastrada para este questionário ainda.</p>
                    </c:otherwise>
                </c:choose>
                
                <div class="form-actions" style="margin-top: 30px;">
                    <a href="${pageContext.request.contextPath}/empreendimento/plano-marketing/pdf" 
                       id="btnGerarPdf" 
                       class="btn-secondary" 
                       style="padding: 12px 25px; font-size: 1.4rem; font-weight: 600;">
                       <i class='bx bxs-file-pdf'></i> Gerar PDF
                    </a>
                    <button type="submit">Salvar Questionário</button>
                </div>
            </form>
        </div>
    </section>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('form-questionario');
            const btnGerarPdf = document.getElementById('btnGerarPdf');
            const pdfUrl = btnGerarPdf.href; // Guarda a URL original do PDF

            // Salva no blur (como antes)
            document.querySelectorAll('input.form-control, textarea.form-control').forEach(item => {
                item.addEventListener('blur', () => {
                    const formData = new FormData();
                    formData.append(item.name, item.value);
                    
                    fetch(form.action, {
                        method: 'POST',
                        body: new URLSearchParams(formData)
                    }).catch(error => console.error('Erro ao salvar no blur:', error));
                });
            });

            // Ação do botão Gerar PDF
            btnGerarPdf.addEventListener('click', function(event) {
                event.preventDefault(); // Impede o link de navegar imediatamente
                
                // Pega TODOS os dados do formulário
                const formData = new FormData(form);
                
                // Mostra um feedback visual (opcional)
                btnGerarPdf.innerHTML = '<i class="bx bx-loader-alt bx-spin"></i> Salvando...';
                btnGerarPdf.style.pointerEvents = 'none'; // Desabilita clique repetido

                // Envia TODOS os dados para salvar
                fetch(form.action, {
                    method: 'POST',
                    body: new URLSearchParams(formData)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Falha ao salvar antes de gerar o PDF');
                    }
                    // Se salvou com sucesso, redireciona para o PDF
                    window.location.href = pdfUrl; 
                })
                .catch(error => {
                    console.error('Erro ao salvar antes do PDF:', error);
                    alert('Erro ao salvar os dados antes de gerar o PDF. Tente novamente.');
                })
                .finally(() => {
                     // Restaura o botão mesmo se der erro
                     setTimeout(() => { // Pequeno delay para garantir que a navegação ocorra se sucesso
                        btnGerarPdf.innerHTML = '<i class="bx bxs-file-pdf"></i> Gerar PDF';
                        btnGerarPdf.style.pointerEvents = 'auto';
                     }, 500);
                });
            });
        });
    </script>
</body>
</html>