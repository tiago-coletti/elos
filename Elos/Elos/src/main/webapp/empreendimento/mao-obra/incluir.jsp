<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
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
</head>
  
<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	
	<div class="inclusao-container">
	
	    <a href="javascript:history.back()" class="back-button">
	        <i class='bx bx-arrow-back'></i>
	        <span>Voltar</span>
	    </a>
	    
	    <div class="inclusao-body">
	        <h2>Inclusão de Mão de Obra</h2>
	        
	        <form action="${pageContext.request.contextPath}/empreendimento/mao-obra/incluir" method="post">
	            <div class="form-group">
	                <label for="nome">Nome</label>
	                <input type="text" id="nome" name="nome" placeholder="Ex: Colheita e Preparo" required>
	            </div>
				<div class="form-group">
				    <label for="custoHora">Custo por Hora</label>
				    <div class="input-prefix-container">
				        <span class="prefix">R$</span>
				        <input type="number" step="0.01" min=0 id="custoHora" name="custoHora" value="${maoObra.custoHora}" required>
				    </div>
				</div>
	            <div class="form-actions">
	                <button type="submit">Cadastrar</button>
	            </div>
	        </form>
	        
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>