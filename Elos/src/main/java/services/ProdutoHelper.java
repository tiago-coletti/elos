package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.dao.ProdutoDAO;
import model.entity.Produto;

public class ProdutoHelper {

    private final ProdutoDAO produtoDAO;

    // Constantes para as regras de negócio do dashboard de produtos
    private static final int LIMITE_CARDS_RENTAVEIS = 2;
    private static final int LIMITE_MODAL_RENTAVEIS = 5;
    private static final int LIMITE_MODAL_VARIACAO = 5;
    
    public ProdutoHelper() {
        this.produtoDAO = new ProdutoDAO();
    }

    public Map<String, Object> prepararDadosDashboard(int empreendimentoId) {
        Map<String, Object> dashboardData = new HashMap<>();

        // --- DADOS PARA OS CARDS E MODAIS ---

        // Card e Modal: Rentabilidade de Produtos
        // Busca os 2 mais rentáveis para o card
        ArrayList<Map<String, Object>> produtosMaisRentaveisCard = produtoDAO.listarProdutosMaisRentaveis(empreendimentoId, LIMITE_CARDS_RENTAVEIS);
        dashboardData.put("produtosMaisRentaveisCard", produtosMaisRentaveisCard);
        
        // Busca os 5 mais rentáveis para o modal
        ArrayList<Map<String, Object>> todosMaisRentaveis = produtoDAO.listarProdutosMaisRentaveis(empreendimentoId, LIMITE_MODAL_RENTAVEIS);
        dashboardData.put("todosMaisRentaveis", todosMaisRentaveis);

        // Card e Modal: Saúde Financeira (Mês)
        double receitaTotalMes = produtoDAO.calcularReceitaTotalPorPeriodo(empreendimentoId, "mes");
        dashboardData.put("receitaTotalMes", receitaTotalMes);

        double custoProducaoMes = produtoDAO.calcularCustoProducaoTotalPorPeriodo(empreendimentoId, "mes");
        dashboardData.put("custoProducaoMes", custoProducaoMes);

        // Card e Modal: Variação de Vendas
        ArrayList<Map<String, Object>> variacaoVendas = produtoDAO.analisarVariacaoVendas(empreendimentoId, LIMITE_MODAL_VARIACAO);
        dashboardData.put("variacaoVendas", variacaoVendas);

        // Modal: Composição do Preço Justo
        // Pega o produto mais rentável e busca seus detalhes para o modal
        if (!todosMaisRentaveis.isEmpty()) {
            // Obtemos o ID do produto mais rentável do ranking que acabamos de buscar
            int produtoMaisRentavelId = (int) todosMaisRentaveis.get(0).get("id");
            
            // Usamos o DAO para buscar todos os detalhes (insumos, mao de obra) desse produto
            Produto produtoDetalhado = produtoDAO.obterProdutoPorId(produtoMaisRentavelId, empreendimentoId);
            dashboardData.put("produtoPrecoJusto", produtoDetalhado);
        } else {
        	dashboardData.put("produtoPrecoJusto", null);
        }

        return dashboardData;
    }
}