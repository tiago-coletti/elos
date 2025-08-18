<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
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
	        <h2>Editar Insumo</h2>
	        
	        <form action="${pageContext.request.contextPath}/empreendimento/insumo/editar" method="post">
	        	<input type="hidden" name="id" value="${insumo.id}">
	        	<div class="form-group">
	                <label for="nome">Nome</label>
	                <input type="text" id="nome" name="nome" value="${insumo.nome}" required>
	            </div>
	            <div class="form-group">
	                <label for="unidadeMedida">Unidade de Medida</label>
	                <input type="text" id="unidadeMedida" name="unidadeMedida" value="${insumo.unidadeMedida}" required>
	            </div>
	            <div class="form-actions form-actions-edicao">
	                <button type="submit" class="button-edicao">Editar</button>
	            </div>
	        </form>
	        
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>