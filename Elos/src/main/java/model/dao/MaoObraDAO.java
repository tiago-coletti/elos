package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.MaoObra;
import util.ConnectionFactory;

public class MaoObraDAO {
	private static final Logger logger = Logger.getLogger(MaoObraDAO.class.getName());
	
	/**
	 * Insere uma nova mão de obra na tabela "mao_obra".
	 * @param maoObra Objeto MaoObra contendo os dados da nova mão de obra.
	 */
	public void incluirMaoObra(MaoObra maoObra) {
		String sqlIncluirMaoObra = "INSERT INTO mao_obra (nome, custo_hora, empreendimento_id) VALUES (?, ?, ?)";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlIncluirMaoObra)) {
			pstmt.setString(1, maoObra.getNome());
			pstmt.setDouble(2, maoObra.getCustoHora());
			pstmt.setInt(3, maoObra.getEmpreendimentoId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao incluir mão de obra: " + maoObra.getNome(), e);
		}
	}

	/**
	 * Atualiza os dados de uma mão de obra existente na tabela "mao_obra".
	 * @param maoObra Objeto MaoObra contendo os dados atualizados.
	 */
	public void editarMaoObra(MaoObra maoObra) {
		String sqlEditarMaoObra = "UPDATE mao_obra SET nome = ?, custo_hora = ? WHERE id = ? AND empreendimento_id = ?";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlEditarMaoObra)) {
			pstmt.setString(1, maoObra.getNome());
			pstmt.setDouble(2, maoObra.getCustoHora());
			pstmt.setInt(3, maoObra.getId());
			pstmt.setInt(4, maoObra.getEmpreendimentoId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao atualizar mão de obra com ID: " + maoObra.getId(), e);
		}
	}

	/**
	 * Exclui uma mão de obra da tabela "mao_obra" (soft delete).
	 * @param id ID da mão de obra a ser excluída.
	 * @param empreendimentoId ID do empreendimento para validação.
	 * @return true se a mão de obra foi excluída, false caso contrário.
	 */
	public boolean excluirMaoObra(int id, int empreendimentoId) {
		String sqlExcluirMaoObra = "UPDATE mao_obra SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlExcluirMaoObra)) {
			pstmt.setInt(1, id);
			pstmt.setInt(2, empreendimentoId);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao excluir mão de obra com ID: " + id, e);
		}
		return false;
	}

	/**
	 * Recupera uma mão de obra pelo ID.
	 * @param id ID da mão de obra a ser recuperada.
	 * @param empreendimentoId ID do empreendimento para validação.
	 * @return Objeto MaoObra com os dados da mão de obra ou null caso não encontrada.
	 */
	public MaoObra obterMaoObraPorId(int id, int empreendimentoId) {
		MaoObra maoObra = null;
		String sqlObterMaoObra = "SELECT * FROM mao_obra WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlObterMaoObra)) {

			pstmt.setInt(1, id);
			pstmt.setInt(2, empreendimentoId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					maoObra = new MaoObra();
					maoObra.setId(rs.getInt("id"));
					maoObra.setNome(rs.getString("nome"));
					maoObra.setCustoHora(rs.getDouble("custo_hora"));
				}
			}

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao obter mão de obra com ID: " + id, e);
		}

		return maoObra;
	}

	/**
	 * Recupera todas as mãos de obra ativas da tabela "mao_obra" de determinado empreendimento.
	 * @param empreendimentoId ID do empreendimento.
	 * @return Lista de mãos de obra cadastradas no banco de dados.
	 */
	public ArrayList<MaoObra> listarMaosObra(int empreendimentoId) {
		ArrayList<MaoObra> maosObra = new ArrayList<>();
		String sqlListarMaosObra = "SELECT * FROM mao_obra WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY nome ASC";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlListarMaosObra)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				MaoObra maoObra = new MaoObra();
				maoObra.setId(rs.getInt("id"));
				maoObra.setNome(rs.getString("nome"));
				maoObra.setCustoHora(rs.getDouble("custo_hora"));
				if (rs.getTimestamp("created_at") != null) {
					maoObra.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				}
				maosObra.add(maoObra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar mãos de obra.", e);
		}
		return maosObra;
	}

	/**
	 * Lista as mãos de obra com maior custo por hora.
	 * @param empreendimentoId O ID do empreendimento.
	 * @param limite O número máximo de resultados a retornar.
	 * @return Uma lista de objetos MaoObra ordenados pelo custo/hora decrescente.
	 */
	public ArrayList<MaoObra> listarMaisCaras(int empreendimentoId, int limite) {
		ArrayList<MaoObra> maosObra = new ArrayList<>();
		String sql = "SELECT * FROM mao_obra WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY custo_hora DESC LIMIT ?";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				MaoObra maoObra = new MaoObra();
				maoObra.setId(rs.getInt("id"));
				maoObra.setNome(rs.getString("nome"));
				maoObra.setCustoHora(rs.getDouble("custo_hora"));
				maosObra.add(maoObra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar mãos de obra mais caras.", e);
		}
		return maosObra;
	}
	
	/**
	 * Lista as mãos de obra mais utilizadas em produtos (baseado em horas registradas).
	 * @param empreendimentoId O ID do empreendimento.
	 * @param limite O número máximo de resultados a retornar.
	 * @return Uma lista de Mapas, cada um contendo 'nome' (String) e 'total_horas' (Double).
	 */
	public ArrayList<Map<String, Object>> listarMaisUtilizadas(int empreendimentoId, int limite) {
		ArrayList<Map<String, Object>> maisUtilizadas = new ArrayList<>();
		String sql = "SELECT mo.nome, SUM(pmo.horas_utilizadas) as total_horas "
				   + "FROM produto_mao_obra pmo "
				   + "JOIN mao_obra mo ON pmo.mao_obra_id = mo.id "
				   + "WHERE mo.empreendimento_id = ? AND mo.deleted_at IS NULL "
				   + "GROUP BY mo.id, mo.nome "
				   + "ORDER BY total_horas DESC "
				   + "LIMIT ?";
		
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> item = new HashMap<>();
				item.put("nome", rs.getString("nome"));
				item.put("total_horas", rs.getDouble("total_horas"));
				maisUtilizadas.add(item);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar mãos de obra mais utilizadas.", e);
		}
		return maisUtilizadas;
	}
	
	/**
	 * Lista as últimas mãos de obra cadastradas.
	 * @param empreendimentoId O ID do empreendimento.
	 * @param limite O número máximo de resultados a retornar.
	 * @return Uma lista de objetos MaoObra ordenados pela data de criação decrescente.
	 */
	public ArrayList<MaoObra> listarUltimasCadastradas(int empreendimentoId, int limite) {
		ArrayList<MaoObra> maosObra = new ArrayList<>();
		String sql = "SELECT * FROM mao_obra WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ?";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				MaoObra maoObra = new MaoObra();
				maoObra.setId(rs.getInt("id"));
				maoObra.setNome(rs.getString("nome"));
				maoObra.setCustoHora(rs.getDouble("custo_hora"));
				if (rs.getTimestamp("created_at") != null) {
					maoObra.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				}
				maosObra.add(maoObra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar últimas mãos de obra cadastradas.", e);
		}
		return maosObra;
	}
}