package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.dao.InsumoDAO;
import model.entity.Insumo;

public class InsumoHelper {

	private final InsumoDAO insumoDAO;

	// Constantes para as regras de negócio do dashboard
	private static final double ESTOQUE_BAIXO_LIMITE = 10.0;
	private static final int DIAS_ESTOQUE_PARADO = 90;
	private static final int LIMITE_CARDS = 2; 

	public InsumoHelper() {
		this.insumoDAO = new InsumoDAO();
	}

	public Map<String, Object> prepararDadosDashboard(int empreendimentoId) {
		Map<String, Object> dashboardData = new HashMap<>();

		// Card: Insumos com Estoque Baixo
		ArrayList<Insumo> insumosEstoqueBaixo = insumoDAO.listarInsumosComEstoqueBaixo(empreendimentoId,
				ESTOQUE_BAIXO_LIMITE);
		dashboardData.put("insumosEstoqueBaixo", insumosEstoqueBaixo);
		dashboardData.put("insumosEstoqueBaixoCard",
				insumosEstoqueBaixo.subList(0, Math.min(insumosEstoqueBaixo.size(), LIMITE_CARDS)));

		// Card: Insumos Parados (Oportunidade de Troca)
		ArrayList<Insumo> insumosParados = insumoDAO.listarInsumosParados(empreendimentoId, DIAS_ESTOQUE_PARADO);
		dashboardData.put("insumosParados", insumosParados);

		// Card: Valor Total do Estoque
		double valorTotalEstoque = insumoDAO.calcularValorTotalEstoque(empreendimentoId);
		dashboardData.put("valorTotalEstoque", valorTotalEstoque);

		// Card: Últimos Insumos Adicionados
		ArrayList<Insumo> ultimosInsumosAdicionados = insumoDAO.listarUltimosInsumosAdicionados(empreendimentoId,
				LIMITE_CARDS);
		dashboardData.put("ultimosInsumosAdicionados", ultimosInsumosAdicionados);
		
		return dashboardData;
	}
}