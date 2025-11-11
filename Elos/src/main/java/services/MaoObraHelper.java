package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.dao.MaoObraDAO;
import model.entity.MaoObra;

public class MaoObraHelper {

	private final MaoObraDAO maoObraDAO;

	private static final int LIMITE_CARDS = 3;
	private static final int LIMITE_MODAL = 5;

	public MaoObraHelper() {
		this.maoObraDAO = new MaoObraDAO();
	}

	public Map<String, Object> prepararDadosDashboard(int empreendimentoId) {
		Map<String, Object> dashboardData = new HashMap<>();

		ArrayList<Map<String, Object>> maisUtilizadas = maoObraDAO.listarMaisUtilizadas(empreendimentoId, LIMITE_CARDS);
		dashboardData.put("maisUtilizadas", maisUtilizadas);

		ArrayList<Map<String, Object>> todasMaisUtilizadas = maoObraDAO.listarMaisUtilizadas(empreendimentoId, LIMITE_MODAL);
		dashboardData.put("todasMaisUtilizadas", todasMaisUtilizadas);

		ArrayList<MaoObra> maisCaras = maoObraDAO.listarMaisCaras(empreendimentoId, LIMITE_CARDS);
		dashboardData.put("maisCaras", maisCaras);

		ArrayList<MaoObra> todasMaisCaras = maoObraDAO.listarMaisCaras(empreendimentoId, LIMITE_MODAL);
		dashboardData.put("todasMaisCaras", todasMaisCaras);
		
		ArrayList<MaoObra> ultimasCadastradas = maoObraDAO.listarUltimasCadastradas(empreendimentoId, LIMITE_MODAL);
        dashboardData.put("ultimasCadastradas", ultimasCadastradas);

		return dashboardData;
	}
}