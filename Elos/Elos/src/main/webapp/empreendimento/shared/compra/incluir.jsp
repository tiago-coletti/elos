<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Insumo"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.gson.Gson"%>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Insumo> insumos = (ArrayList<Insumo>) request.getAttribute("insumos");

    String insumosJson = "[]";
    if (insumos != null) {
        insumosJson = new Gson().toJson(insumos);
    }
%>
	
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/formulario.css" />
</head>
  
<body data-insumos='<%= insumosJson %>'>	
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	<div class="inclusao-container">
	
	    <a href="javascript:history.back()" class="back-button">
	        <i class='bx bx-arrow-back'></i>
	        <span>Voltar</span>
	    </a>
	    
	    <div class="inclusao-body">
	        <h2>Inclusão de Compra</h2>
	        
	        <form id="compraForm" action="${pageContext.request.contextPath}/empreendimento/compra/incluir" method="post">
	            
                <div class="form-group">
                    <label for="dataCompra">Data da Compra</label>
                    <input type="date" id="dataCompra" name="dataCompra" required>
                </div>

                <div class="compra-layout">
                    <div class="selecao-insumos">
                        <h3>Insumos Disponíveis</h3>
                        <input type="text" id="searchInput" class="form-group input" placeholder="Pesquisar insumo...">
                        <div class="insumo-disponivel-list" id="insumoList">
                            </div>
                    </div>

                    <div class="itens-compra-container">
                        <h3>Itens da Compra</h3>
                        <table class="itens-compra-table">
                            <thead>
                                <tr>
                                    <th>Insumo</th>
                                    <th>Quantidade</th>
                                    <th>Valor Unitário</th>
                                    <th>Subtotal</th>
                                    <th>Ação</th>
                                </tr>
                            </thead>
                            <tbody id="itensCompraBody">
                                <tr id="itens-compra-vazio">
                                    <td colspan="5">Nenhum item adicionado</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="compra-footer">
                    <div class="total-compra-container">
                        Valor Total: <span id="valorTotal">R$ 0,00</span>
                    </div>
                    <div class="form-actions">
                        <button type="submit">Cadastrar Compra</button>
                    </div>
                </div>
	        </form>
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/compra.js"></script>
</body>
</html>