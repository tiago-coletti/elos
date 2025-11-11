package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.dao.VendaDAO;
import model.entity.Venda;

public class VendaHelper {

    private final VendaDAO vendaDAO;

    private static final int LIMITE_CARDS_VENDAS = 2;
    private static final int LIMITE_CARDS_PRODUTOS = 3;
    private static final int LIMITE_MODAL_VENDAS = 5;
    private static final int LIMITE_MODAL_PRODUTOS = 5;

    public VendaHelper() {
        this.vendaDAO = new VendaDAO();
    }

    public Map<String, Object> prepararDadosDashboard(int empreendimentoId) {
        Map<String, Object> dashboardData = new HashMap<>();

        double totalVendidoMes = vendaDAO.calcularTotalVendidoPorPeriodo(empreendimentoId, "mes");
        dashboardData.put("totalVendidoMes", totalVendidoMes);
        
        double totalVendidoAno = vendaDAO.calcularTotalVendidoPorPeriodo(empreendimentoId, "ano");
        dashboardData.put("totalVendidoAno", totalVendidoAno);

        int contagemVendasMes = vendaDAO.contarVendasPorPeriodo(empreendimentoId, "mes");
        dashboardData.put("contagemVendasMes", contagemVendasMes);
        
        int contagemVendasAno = vendaDAO.contarVendasPorPeriodo(empreendimentoId, "ano");
        dashboardData.put("contagemVendasAno", contagemVendasAno);

        ArrayList<Venda> todasVendas = vendaDAO.listarVendas(empreendimentoId);
        
        List<Venda> ultimasVendas = todasVendas.stream().limit(LIMITE_CARDS_VENDAS).collect(Collectors.toList());
        dashboardData.put("ultimasVendas", ultimasVendas);
        
        List<Venda> todasUltimasVendas = todasVendas.stream().limit(LIMITE_MODAL_VENDAS).collect(Collectors.toList());
        dashboardData.put("todasUltimasVendas", todasUltimasVendas);

        ArrayList<Map<String, Object>> produtosMaisVendidos = vendaDAO.listarProdutosMaisVendidos(empreendimentoId, "mes", LIMITE_CARDS_PRODUTOS);
        dashboardData.put("produtosMaisVendidos", produtosMaisVendidos);
        
        ArrayList<Map<String, Object>> todosProdutosMaisVendidos = vendaDAO.listarProdutosMaisVendidos(empreendimentoId, "mes", LIMITE_MODAL_PRODUTOS);
        dashboardData.put("todosProdutosMaisVendidos", todosProdutosMaisVendidos);

        return dashboardData;
    }
}