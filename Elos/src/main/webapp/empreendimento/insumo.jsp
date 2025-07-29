<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<!DOCTYPE html><html><head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/carrossel.css" />
</head><body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

    <main class="container">
        <section class="carousel-container">
            <%-- O botão anterior foi removido daqui --%>
            <div class="carousel-wrapper">
                <div class="carousel-track">

                    <div class="card">
                        <h3>Insumos com Estoque Baixo</h3>
                        <p>Total de <span class="card-value">5</span> insumos abaixo do mínimo.</p>
                        <ul>
                            <li><strong>Parafuso M6:</strong> 15 unidades</li>
                            <li><strong>Tinta Branca (Litro):</strong> 2 unidades</li>
                        </ul>
                        <button class="view-details-btn action-button" data-modal-target="lowStockModal">Solicitar Reposição</button>
                    </div>

                    <div class="card">
                        <h3>Insumos Próximos do Vencimento</h3>
                        <p>Itens a vencer nos próximos <strong>30, 60 e 90 dias</strong>.</p>
                        <ul>
                            <li><strong>30 Dias:</strong> 3 itens</li>
                            <li><strong>60 Dias:</strong> 5 itens</li>
                            <li><strong>90 Dias:</strong> 2 itens</li>
                        </ul>
                        <button class="view-details-btn" data-modal-target="expirationModal">Ver Detalhes</button>
                    </div>

                    <div class="card">
                        <h3>Valor Total do Estoque</h3>
                        <p class="card-value">R$ 150.000,00</p>
                        <p>Valor monetário total de todos os insumos.</p>
                    </div>

                    <div class="card">
                        <h3>Insumos Mais Utilizados</h3>
                        <p>Período: <select><option>Mês</option><option>Semana</option><option>Trimestre</option></select></p>
                        <ul>
                            <li><strong>Cimento CP II:</strong> 120 sacos</li>
                            <li><strong>Areia Fina:</strong> 50 m³</li>
                        </ul>
                    </div>

                    <div class="card">
                        <h3>Últimos Insumos Adicionados</h3>
                        <ul>
                            <li><strong>Madeira de Lei (5m):</strong> 2025-07-20</li>
                            <li><strong>Cano PVC (100mm):</strong> 2025-07-18</li>
                        </ul>
                    </div>

                    <div class="card">
                        <h3>Insumos com Estoque Parado <i class='bx bx-help-circle help-icon' data-modal-target="stagnantStockHelpModal"></i></h3>
                        <p>5 insumos sem movimentação há mais de <strong>90 dias</strong>.</p>
                        <button class="view-details-btn" data-modal-target="stagnantStockModal">Ver Todos</button>
                    </div>

                    <div class="card">
                        <h3>Previsão de Consumo <i class='bx bx-help-circle help-icon' data-modal-target="consumptionForecastHelpModal"></i></h3>
                        <p><strong>Cimento CP II:</strong> Acabará em ~35 dias</p>
                        <p><strong>Tijolo Baiano:</strong> Acabará em ~60 dias</p>
                        <button class="view-details-btn" data-modal-target="consumptionForecastModal">Ver Detalhes</button>
                    </div>

                </div>
            </div>
            <%-- Botão de próxima com ícone Boxicons --%>
            <button class="carousel-button next-button"><i class='bx bx-chevron-right'></i></button>
        </section>
    </main>
    
    <%@ include file="/empreendimento/shared/modals/insumo.jspf"%>

    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/carrossel.js"></script>
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>