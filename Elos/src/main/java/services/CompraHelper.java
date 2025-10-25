package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.dao.CompraDAO;
import model.dao.InsumoDAO;
import model.entity.Compra;
import model.entity.Insumo;

public class CompraHelper {

    private final CompraDAO compraDAO;
    private final InsumoDAO insumoDAO;
    
    // Constantes para as regras de negócio do dashboard
    private static final double ESTOQUE_BAIXO_LIMITE = 10.0;
    private static final int LIMITE_CARDS_COMPRAS = 2;
    private static final int LIMITE_CARDS_VARIACAO = 2;
    private static final int LIMITE_CARDS_DESPESAS = 2;

    public CompraHelper() {
        this.compraDAO = new CompraDAO();
        this.insumoDAO = new InsumoDAO(); // Reutilizamos o InsumoDAO
    }

    public Map<String, Object> prepararDadosDashboard(int empreendimentoId) {
        Map<String, Object> dashboardData = new HashMap<>();

        // Card: Total Gasto em Compras (Exemplo: último mês)
        // Você pode adicionar lógica para trocar o período (mês, trimestre, ano)
        double totalGastoMes = compraDAO.calcularTotalGastoPorPeriodo(empreendimentoId, "mes");
        dashboardData.put("totalGastoMes", totalGastoMes);
        
        // Card: Últimas Compras Realizadas
        ArrayList<Compra> ultimasCompras = compraDAO.listarUltimasCompras(empreendimentoId, LIMITE_CARDS_COMPRAS);
        dashboardData.put("ultimasCompras", ultimasCompras);

        // Card: Variação de Preços
        ArrayList<Map<String, Object>> variacaoPrecos = compraDAO.analisarVariacaoPrecos(empreendimentoId, LIMITE_CARDS_VARIACAO);
        dashboardData.put("variacaoPrecos", variacaoPrecos);

        // Card: Sugestões de Compra (baseado no estoque baixo)
        ArrayList<Insumo> insumosEstoqueBaixo = insumoDAO.listarInsumosComEstoqueBaixo(empreendimentoId, ESTOQUE_BAIXO_LIMITE);
        dashboardData.put("sugestoesCompra", insumosEstoqueBaixo);

        // Card: Maiores Despesas (Mês)
        ArrayList<Map<String, Object>> maioresDespesas = compraDAO.listarMaioresDespesas(empreendimentoId, "mes", LIMITE_CARDS_DESPESAS);
        dashboardData.put("maioresDespesas", maioresDespesas);
        
        // Card: Nível de Estoque Atual (reutilizando métodos do InsumoDAO)
        double valorTotalEstoque = insumoDAO.calcularValorTotalEstoque(empreendimentoId);
        int totalItensDistintos = insumoDAO.contarInsumosDistintos(empreendimentoId);
        dashboardData.put("valorTotalEstoque", valorTotalEstoque);
        dashboardData.put("totalItensDistintos", totalItensDistintos);
        
        // --- DADOS PARA OS MODAIS ---
        // Lista completa para o modal de últimas compras
        ArrayList<Compra> todasUltimasCompras = compraDAO.listarUltimasCompras(empreendimentoId, 5); // Ex: 5 no modal
        dashboardData.put("todasUltimasCompras", todasUltimasCompras);

        // Lista completa para o modal de maiores despesas
        ArrayList<Map<String, Object>> todasMaioresDespesas = compraDAO.listarMaioresDespesas(empreendimentoId, "mes", 5); // Ex: 5 no modal
        dashboardData.put("todasMaioresDespesas", todasMaioresDespesas);
        
        return dashboardData;
    }
}