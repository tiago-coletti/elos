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
  
  <style>
    .integrantes-container { display: flex; flex-direction: column; gap: 15px; }
    .integrante-item { display: grid; grid-template-columns: 2fr 1fr auto; gap: 15px; align-items: center; }
    .integrante-item .input-field { margin-bottom: 0; }
    .integrante-item .remove-btn { height: 45px; width: 45px; border: none; background-color: var(--button-danger-color); color: #fff; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 1.2rem; transition: background-color 0.3s ease; }
    .integrante-item .remove-btn:hover { background-color: var(--button-danger-hover-color); }
    .add-button-container { margin-top: 15px; }
  </style>
</head>
<body>
    <%@ include file="/empreendimento/shared/navbar.jspf"%>
    <main class="inclusao-container">
        <a href="${pageContext.request.contextPath}/empreendimento/dashboard-principal" class="back-button">
            <i class='bx bx-arrow-back'></i> Voltar
        </a>
        <div class="inclusao-body">
            <h2>Minha Conta/Perfil</h2>
            <form action="${pageContext.request.contextPath}/empreendimento/atualizarConta" method="POST">
                <h3 class="subtitle">Informações do Empreendimento</h3>
                <div class="form-group">
                    <label for="nome">Nome do Empreendimento</label>
                    <input type="text" id="nome" name="nome" placeholder="Digite o nome do empreendimento" required value="<c:out value="${empreendimento.nome}"/>" />
                </div>
                <div class="form-group">
                    <label for="email">Email de Contato</label>
                    <input type="email" id="email" name="email" placeholder="Digite o email de contato" required value="<c:out value="${empreendimento.email}"/>" />
                </div>
                <div class="form-group">
                    <label for="phoneNumber">Telefone</label>
                    <input type="tel" id="phoneNumber" name="phoneNumber" placeholder="(xx) 9 xxxx-xxxx" maxlength="16" pattern="\(\d{2}\) 9 \d{4}-\d{4}" title="O telefone deve estar no formato (xx) 9 xxxx-xxxx" value="<c:out value="${empreendimento.numeroTelefone}"/>" />
                </div>
                <div class="form-group">
                    <label for="city">Cidade</label>
                    <input type="text" id="city" name="city" placeholder="Digite a cidade" value="<c:out value="${empreendimento.cidade}"/>" />
                </div>
                <h3 class="subtitle">Informações da Conta</h3>
                <div class="form-group">
                    <label for="loginEmpreendimento">Login do Empreendimento</label>
                    <input type="text" id="loginEmpreendimento" name="loginEmpreendimento" placeholder="Seu Login" required readonly value="<c:out value="${empreendimento.login}"/>" style="background-color: var(--input-disabled-color); cursor: not-allowed;"/> 
                </div>
                <h3 class="subtitle">Alterar Senha (Preencha somente se for alterar)</h3>
                <div class="form-group">
                    <label for="novaSenha">Nova Senha</label>
                    <input type="password" id="novaSenha" name="novaSenha" class="password" placeholder="Digite a nova senha" />
                </div>
                <div class="form-group">
                    <label for="confirmarNovaSenha">Confirmar Nova Senha</label>
                    <input type="password" id="confirmarNovaSenha" name="confirmarNovaSenha" class="password" placeholder="Confirme a nova senha" />
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
                        <input type="text" id="anoSemestre" name="anoSemestre" placeholder="Ex: 2024/1" required value="<c:out value="${empreendimento.anoSemestre}"/>" maxlength="6" pattern="\d{4}\/[1-2]" title="O Semestre deve seguir o padrão AAAA/S (ex: 2024/1 ou 2023/2)"/>
                    </div>
                    <h3 class="subtitle">Integrantes do Empreendimento</h3>
                    <div class="form-group" style="padding: 15px; border: 1px dashed var(--border-color); border-radius: 8px;">
                        <input type="hidden" name="alunosParaRemover" id="alunosParaRemover" />
                        <div id="integrantes-container" class="integrantes-container">
                            <c:if test="${empty empreendimento.alunos}">
                                <p id="nenhum-integrante-msg" style="font-style: italic;">Nenhum integrante cadastrado.</p>
                            </c:if>
                            <c:forEach var="aluno" items="${empreendimento.alunos}">
                                <div class="integrante-item">
                                    <input type="hidden" name="alunoId" value="${aluno.id}" />
                                    <div class="input-field"><input type="text" name="alunoNome" placeholder="Nome do integrante" required value="<c:out value="${aluno.nome}"/>" /></div>
                                    <div class="input-field"><input type="text" name="alunoMatricula" placeholder="Matrícula" required value="<c:out value="${aluno.matricula}"/>" /></div>
                                    <button type="button" class="remove-btn" title="Remover Integrante" onclick="removerIntegrante(this, ${aluno.id})"><i class='uil uil-trash-alt'></i></button>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="input-field button add-button-container">
                             <button type="button" id="add-aluno-btn" onclick="adicionarIntegrante()">Adicionar Integrante</button>
                        </div>
                    </div>
                </c:if>
                <div class="form-actions">
                    <button type="submit"><i class="uil uil-save" style="margin-right: 5px;"></i> Salvar Alterações</button>
                </div>
            </form>
        </div>
    </main>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/conta.js"></script>
    <script>
        function adicionarIntegrante() {
            const msgNenhum = document.getElementById('nenhum-integrante-msg');
            if (msgNenhum) {
                msgNenhum.remove();
            }

            const container = document.getElementById('integrantes-container');
            const novoItem = document.createElement('div');
            novoItem.className = 'integrante-item';
            
            novoItem.innerHTML = `
                <input type="hidden" name="alunoId" value="0" />
                <div class="input-field"><input type="text" name="alunoNome" placeholder="Nome do novo integrante" required /></div>
                <div class="input-field"><input type="text" name="alunoMatricula" placeholder="Matrícula" required /></div>
                <button type="button" class="remove-btn" title="Remover" onclick="removerNovoIntegrante(this)"><i class='uil uil-trash-alt'></i></button>
            `;
            
            container.appendChild(novoItem);
        }

        function removerIntegrante(buttonElement, alunoId) {
            const idsParaRemoverInput = document.getElementById('alunosParaRemover');
            const currentIds = idsParaRemoverInput.value ? idsParaRemoverInput.value.split(',') : [];
            if (!currentIds.includes(String(alunoId))) {
                 currentIds.push(alunoId);
            }
            idsParaRemoverInput.value = currentIds.join(',');

            const itemParaRemover = buttonElement.closest('.integrante-item');
            itemParaRemover.remove();
        }

        function removerNovoIntegrante(buttonElement) {
            const itemParaRemover = buttonElement.closest('.integrante-item');
            itemParaRemover.remove();
        }
    </script>
</body>
</html>