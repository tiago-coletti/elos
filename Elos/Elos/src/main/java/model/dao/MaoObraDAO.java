package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.MaoObra;
import util.ConnectionFactory;

public class MaoObraDAO {
	private static final Logger logger = Logger.getLogger(MaoObraDAO.class.getName());
	
	/**
	 * Insere uma nova mão de obra na tabela "mao_obra".
	 * 
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
	 * 
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
	 * Exclui uma mão de obra da tabela "mao_obra"
	 * 
	 * @param id ID da mão de obra a ser excluída.
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
	 * 
	 * @param id ID da mão de obra a ser recuperada.
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
	 * Recupera todas as mãos de obra da tabela "mao_obra" de determinado empreendimento.
	 * 
	 * @return Lista de mãos de obra cadastradas no banco de dados.
	 */
	public ArrayList<MaoObra> listarMaosObra(int empreendimentoId) {
		ArrayList<MaoObra> maosObra = new ArrayList<>();
		String sqlListarMaosObra = "SELECT * FROM mao_obra WHERE empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlListarMaosObra)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				MaoObra maoObra = new MaoObra();
				maoObra.setId(rs.getInt("id"));
				maoObra.setNome(rs.getString("nome"));
				maoObra.setCustoHora(rs.getDouble("custo_hora"));
				maosObra.add(maoObra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar mãos de obra.", e);
		}
		return maosObra;
	}
	
}