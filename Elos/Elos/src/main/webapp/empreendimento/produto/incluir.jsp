<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.entity.Insumo"%>
<%@ page import="model.entity.MaoObra"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.gson.Gson"%>
<%
    @SuppressWarnings("unchecked")
    ArrayList<Insumo> insumos = (ArrayList<Insumo>) request.getAttribute("insumos");

    String insumosJson = "[]";
    if (insumos != null) {
        insumosJson = new Gson().toJson(insumos);
    }
    
    @SuppressWarnings("unchecked")
    ArrayList<MaoObra> maosObra = (ArrayList<MaoObra>) request.getAttribute("maosObra");

    String maosObraJson = "[]";
    if (maosObra != null) {
    	maosObraJson = new Gson().toJson(maosObra);
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
  
<body data-insumos='<%= insumosJson %>' data-maos-obra='<%= maosObraJson %>'>	
	<%@ include file="/empreendimento/shared/navbar.jspf"%>
	
	<div class="inclusao-container">
	
	    <a href="javascript:history.back()" class="back-button">
	        <i class='bx bx-arrow-back'></i>
	        <span>Voltar</span>
	    </a>
	    
	    <div class="inclusao-body">
	        <h2>Inclusão de Produto</h2>
	        
	        <form id="produtoForm" action="${pageContext.request.contextPath}/empreendimento/produto/incluir" method="post">
	            
                <div class="form-group">
                    <label for="nomeProduto">Nome do Produto</label>
                    <input type="text" id="nomeProduto" name="nomeProduto" required>
                </div>
                
                <div class="form-group">
                    <label for="precoVenda">Preço de Venda</label>
                    <input type="number" step="0.01" id="precoVenda" name="precoVenda" min="0.01" required>
                </div>
				
                <div class="compra-layout">
                    <div class="selecao-insumos">
                        <h3>Insumos Disponíveis</h3>
                        <div class="form-group">
                            <input type="text" id="searchInputInsumo" class="input" placeholder="Pesquisar insumo...">
                        </div>
                        <div class="insumo-disponivel-list" id="listaInsumosDisponiveis"></div>
                    </div>

                    <div class="itens-compra-container">
                        <h3>Composição de Insumos</h3>
                        <table class="itens-compra-table">
                            <thead>
                                <tr>
                                    <th>Insumo</th>
                                    <th>Quantidade</th>
                                    <th>Custo Unitário</th>
                                    <th>Subtotal</th>
                                    <th>Ação</th>
                                </tr>
                            </thead>
                            <tbody id="itensInsumoBody">
                                <tr id="itens-insumo-vazio">
                                    <td colspan="5">Nenhum insumo adicionado</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
				
                <div class="compra-layout">
                    <div class="selecao-insumos">
                        <h3>Mão de Obra Disponível</h3>
                        <div class="form-group">
                            <input type="text" id="searchInputMaoObra" class="input" placeholder="Pesquisar mão de obra...">
                        </div>
                        <div class="insumo-disponivel-list" id="listaMaosObraDisponiveis"></div>
                    </div>

                    <div class="itens-compra-container">
                        <h3>Composição de Mão de Obra</h3>
                        <table class="itens-compra-table">
                            <thead>
                                <tr>
                                    <th>Mão de Obra</th>
                                    <th>Custo da Hora</th>
                                    <th>Horas (Qnt.)</th>
                                    <th>Subtotal</th>
                                    <th>Ação</th>
                                </tr>
                            </thead>
                            <tbody id="itensMaoObraBody">
                                <tr id="itens-maoobra-vazio">
                                    <td colspan="5">Nenhuma mão de obra adicionada</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="compra-footer">
                    <div class="total-compra-container">
                        Custo Total do Produto: <span id="custoTotalProduto">R$ 0,00</span>
                    </div>
                    <div class="form-actions">
                        <button type="submit">Cadastrar Produto</button>
                    </div>
                </div>
	        </form>
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/produto.js"></script>
    
    <%-- ADICIONE ESTE SCRIPT PARA DEBUGAR NO CONSOLE DO NAVEGADOR --%>
    <script>
      console.log('DEBUG JSP - Dados de Mão de Obra:', <%= maosObraJson %>);
    </script>
</body>
</html>