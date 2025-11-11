<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Produto"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.gson.Gson"%>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Produto> produtos = (ArrayList<Produto>) request.getAttribute("produtos");

    String produtosJson = "[]";
    if (produtos != null) {
        produtosJson = new Gson().toJson(produtos);
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
  
<body data-produtos='<%= produtosJson %>'>	
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	<div class="inclusao-container">
	
	    <a href="javascript:history.back()" class="back-button">
	        <i class='bx bx-arrow-back'></i>
	        <span>Voltar</span>
	    </a>
	    
	    <div class="inclusao-body">
	        <h2>Inclusão de Venda</h2>
	        
	        <form id="vendaForm" action="${pageContext.request.contextPath}/empreendimento/venda/incluir" method="post">
	            
                <div class="form-group">
                    <label for="dataVenda">Data da Venda</label>
                    <input type="date" id="dataVenda" name="dataVenda" required>
                </div>
				
                <div class="compra-layout">
                    <div class="selecao-insumos">
                        <h3>Produtos Disponíveis</h3>
                        <input type="text" id="searchInput" class="form-group input" placeholder="Pesquisar produto...">
                        <div class="insumo-disponivel-list" id="produtoList">
                            </div>
                    </div>

                    <div class="itens-compra-container">
                        <h3>Itens da Venda</h3>
                        <table class="itens-compra-table">
                            <thead>
                                <tr>
                                    <th>Produto</th>
                                    <th>Quantidade</th>
                                    <th>Preço de Venda</th>
                                    <th>Subtotal</th>
                                    <th>Ação</th>
                                </tr>
                            </thead>
                            <tbody id="itensVendaBody">
                                <tr id="itens-venda-vazio">
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
                        <button type="submit">Cadastrar Venda</button>
                    </div>
                </div>
	        </form>
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/venda.js"></script>
</body>
</html>