<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="pt-BR">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://unicons.iconscout.com/release/v4.0.0/css/line.css" />
  <title>Elos</title>
  
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/formulario.css" /> 
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/dashboard.css" /> 
</head>

<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

    <main class="inclusao-container">
    
        <a href="${pageContext.request.contextPath}/empreendimento/dashboard" class="back-button">
            <i class='bx bx-arrow-back'></i> Voltar
        </a>
        
        <div class="inclusao-body">
            <h2>Minha Conta/Perfil</h2>
            
            <form action="${pageContext.request.contextPath}/empreendimento/atualizarConta" method="POST">
                <h3 class="subtitle">Informações do Empreendimento</h3>
                
                <div class="form-group">
                    <label for="nome">Nome do Empreendimento</label>
                    <input type="text" id="nome" name="nome" placeholder="Digite o nome do empreendimento" 
                           required value="<c:out value="${empreendimento.nome}"/>" />
                </div>
                
                <div class="form-group">
                    <label for="email">Email de Contato</label>
                    <input type="email" id="email" name="email" placeholder="Digite o email de contato" 
                           required value="<c:out value="${empreendimento.email}"/>" />
                </div>
                
                <div class="form-group">
                    <label for="phoneNumber">Telefone</label>
                    <input type="text" id="phoneNumber" name="phoneNumber" placeholder="Digite o telefone" 
                           value="<c:out value="${empreendimento.numeroTelefone}"/>" />
                </div>
                
                <div class="form-group">
                    <label for="city">Cidade</label>
                    <input type="text" id="city" name="city" placeholder="Digite a cidade" 
                           value="<c:out value="${empreendimento.cidade}"/>" />
                </div>
                
                <h3 class="subtitle">Informações da Conta</h3>
                
                <div class="form-group">
                    <label for="loginEmpreendimento">Login do Empreendimento</label>
                    <input type="text" id="loginEmpreendimento" name="loginEmpreendimento" 
                           placeholder="Seu Login" required readonly 
                           value="<c:out value="${empreendimento.login}"/>"
                           style="background-color: #f0f0f0; cursor: not-allowed;"/> 
                </div>
                
                <h3 class="subtitle">Alterar Senha (Preencha somente se for alterar)</h3>
                <div class="form-group">
                    <label for="novaSenha">Nova Senha</label>
                    <input type="password" id="novaSenha" name="novaSenha" 
                           placeholder="Digite a nova senha" />
                </div>
                
                <div class="form-group">
                    <label for="confirmarNovaSenha">Confirmar Nova Senha</label>
                    <input type="password" id="confirmarNovaSenha" name="confirmarNovaSenha" 
                           placeholder="Confirme a nova senha" />
                </div>

                <c:if test="${empreendimento.aluno}">
                    <h3 class="subtitle">Dados do Empreendimento Solidário de Alunos</h3>
                    
                    <div class="form-group">
                        <label for="cursoGeralId">Curso do Empreendimento Solidário</label>
                        <select id="cursoGeralId" name="cursoGeralId" required>
                            <option value="" disabled>Selecione o Curso</option>
                            <option value="1" <c:if test="${empreendimento.cursoGeralId == 1}">selected</c:if>>Desenvolvimento de Sistemas</option>
                            <option value="2" <c:if test="${empreendimento.cursoGeralId == 2}">selected</c:if>>Energias Renováveis</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="anoSemestre">Ano/Semestre</label>
                        <input type="text" id="anoSemestre" name="anoSemestre" 
                               placeholder="Ex: 2024/1" required 
                               value="<c:out value="${empreendimento.anoSemestre}"/>"
                               pattern="\d{4}\/[1-2]"
                               title="O Semestre deve seguir o padrão AAAA/S (ex: 2024/1 ou 2023/2)"/>
                    </div>
                    
                    <h3 class="subtitle">Integrantes do Empreendimento</h3>
                    <div class="form-group" style="padding: 15px; border: 1px dashed #ccc; border-radius: 8px;">
                        
                        <c:choose>
					        <c:when test="${not empty empreendimento.alunos}">
					            <p style="margin-bottom: 15px; font-size: 0.9em; color: #555;">
					                Altere o nome e a matrícula dos integrantes abaixo. Para remover um integrante, entre em contato com o suporte.
					            </p>
					            
					            <div class="integrantes-list">
					                <div class="integrante-item header">
					                    <label>Nome do Aluno</label>
					                    <label>Matrícula</label>
					                </div>
					
					                <c:forEach var="aluno" items="${empreendimento.alunos}" varStatus="status">
					                    <div class="integrante-item">
					                        <input type="hidden" name="alunoId" value="${aluno.id}" />
					                        
					                        <input type="text" name="alunoNome" class="integrante-input" 
					                               placeholder="Nome do integrante" required 
					                               value="<c:out value="${aluno.nome}"/>" />
					                               
					                        <input type="text" name="alunoMatricula" class="integrante-input" 
					                               placeholder="Matrícula" required 
					                               value="<c:out value="${aluno.matricula}"/>" />
					                    </div>
					                </c:forEach>
					            </div>
					
					        </c:when>
					        <c:otherwise>
					            <p style="margin-bottom: 10px; font-style: italic;">
					                Nenhum integrante cadastrado.
					            </p>
					        </c:otherwise>
					    </c:choose>
                        
                        <div style="display: flex; justify-content: flex-end; margin-top: 15px;">
                             <button type="button" class="form-actions-button" 
                                     onclick="window.location.href='${pageContext.request.contextPath}/empreendimento/gerenciarAlunos'"
                                     style="background-color: var(--grey-color); color: var(--white-color); border: none; padding: 10px 15px; border-radius: 6px; cursor: pointer;">
                                Gerenciar Integrantes
                            </button>
                        </div>
                    </div>
                </c:if>
                	
                <div class="form-actions">
                    <button type="submit">
                         <i class="uil uil-save" style="margin-right: 5px;"></i> Salvar Alterações
                    </button>
                </div>
            </form>
            
        </div>
        
	</main>
    
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/conta.js"></script>

</body>
</html>