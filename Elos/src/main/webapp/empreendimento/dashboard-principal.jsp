<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- Boxicons CSS -->
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/dashboard.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/botao.css" />
</head>
  
<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	<div class="welcome-container">
    	<h2>Bom dia, Fulano!</h2>
    	<div class="info-box">
      		DATA: 00/00/00 &nbsp; HORÁRIO: 00:00
    	</div>
	</div>
	
	<div class="button-container">
	    <form action="ServletNovaVenda" method="post">
	        <button type="submit" class="square-button">
	            <i class='bx bx-cart'></i>
	            <span>Nova Venda</span>
	        </button>
	    </form>
	
	    <form action="ServletNovaCompra" method="post">
	        <button type="submit" class="square-button">
	            <i class='bx bx-receipt'></i>
	            <span>Nova Compra</span>
	        </button>
	    </form>
	
	    <form action="ServletEstoque" method="post">
	        <button type="submit" class="square-button">
	            <i class='bx bxs-package'></i>
	            <span>Estoque</span>
	        </button>
	    </form>
	
	    <form action="ServletInvestimentos" method="post">
	        <button type="submit" class="square-button">
	            <i class='bxr bx-piggy-bank'></i>
	            <span>Investimentos</span>
	        </button>
	    </form>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>