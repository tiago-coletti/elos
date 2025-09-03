package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Insumo;
import util.ConnectionFactory;

public class InsumoDAO {
	private static final Logger logger = Logger.getLogger(InsumoDAO.class.getName());

	/**
	 * Insere um novo insumo na tabela "insumo".
	 * 
	 * @param insumo Objeto Insumo contendo os dados do novo insumo.
	 */
	public void incluirInsumo(Insumo insumo) {
		String sqlIncluirInsumo = "INSERT INTO insumo (nome, unidade_medida, empreendimento_id) VALUES (?, ?, ?)";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlIncluirInsumo)) {
			pstmt.setString(1, insumo.getNome());
			pstmt.setString(2, insumo.getUnidadeMedida());
			pstmt.setInt(3, insumo.getEmpreendimentoId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao incluir insumo: " + insumo.getNome(), e);
		}
	}

	/**
	 * Atualiza os dados de um insumo existente na tabela "insumo".
	 * 
	 * @param insumo Objeto Insumo contendo os dados atualizados.
	 */
	public void editarInsumo(Insumo insumo) {
		String sqlEditarInsumo = "UPDATE insumo SET nome = ?, unidade_medida = ? WHERE id = ? AND empreendimento_id = ?";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlEditarInsumo)) {
			pstmt.setString(1, insumo.getNome());
			pstmt.setString(2, insumo.getUnidadeMedida());
			pstmt.setInt(3, insumo.getId());
			pstmt.setInt(4, insumo.getEmpreendimentoId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao atualizar insumo com ID: " + insumo.getId(), e);
		}
	}

	/**
	 * Exclui um insumo da tabela "insumo"
	 * 
	 * @param id ID do insumo a ser excluído.
	 * @return true se o insumo foi excluído, false caso contrário.
	 */
	public boolean excluirInsumo(int id, int empreendimentoId) {
		String sqlExcluirInsumo = "UPDATE insumo SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlExcluirInsumo)) {
			pstmt.setInt(1, id);
			pstmt.setInt(2, empreendimentoId);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao excluir insumo com ID: " + id, e);
		}
		return false;
	}

	/**
	 * Recupera um insumo pelo ID.
	 * 
	 * @param id ID do insumo a ser recuperado.
	 * @return Objeto Insumo com os dados do insumo ou null caso não encontrado.
	 */
	public Insumo obterInsumoPorId(int id, int empreendimentoId) {
		Insumo insumo = null;
		String sqlObterInsumo = "SELECT * FROM insumo WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlObterInsumo)) {

			pstmt.setInt(1, id);
			pstmt.setInt(2, empreendimentoId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					insumo = new Insumo();
					insumo.setId(rs.getInt("id"));
					insumo.setNome(rs.getString("nome"));
					insumo.setUnidadeMedida(rs.getString("unidade_medida"));
					insumo.setQuantidade(rs.getDouble("quantidade"));
				}
			}

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao obter insumo com ID: " + id, e);
		}

		return insumo;
	}

	/**
	 * Recupera todos os insumos da tabela "insumo" de determinado empreendimento.
	 * 
	 * @return Lista de insumos cadastrados no banco de dados.
	 */
	public ArrayList<Insumo> listarInsumos(int empreendimentoId) {
		ArrayList<Insumo> insumos = new ArrayList<>();
		String sqlListarInsumos = "SELECT * FROM insumo WHERE empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlListarInsumos)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Insumo insumo = new Insumo();
				insumo.setId(rs.getInt("id"));
				insumo.setNome(rs.getString("nome"));
				insumo.setUnidadeMedida(rs.getString("unidade_medida"));
				insumo.setQuantidade(rs.getDouble("quantidade"));

				insumos.add(insumo);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar insumos.", e);
		}
		return insumos;
	}
	
	public ArrayList<Insumo> listarInsumosComEstoqueBaixo(int empreendimentoId, double limiteMinimo) {
		ArrayList<Insumo> insumos = new ArrayList<>();
		String sql = "SELECT * FROM insumo WHERE empreendimento_id = ? AND quantidade < ? AND deleted_at IS NULL ORDER BY quantidade ASC";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setDouble(2, limiteMinimo);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Insumo insumo = new Insumo();
				insumo.setId(rs.getInt("id"));
				insumo.setNome(rs.getString("nome"));
				insumo.setUnidadeMedida(rs.getString("unidade_medida"));
				insumo.setQuantidade(rs.getDouble("quantidade"));
				insumos.add(insumo);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar insumos com estoque baixo.", e);
		}
		return insumos;
	}
	
	public ArrayList<Insumo> listarInsumosParados(int empreendimentoId, int diasSemMovimento) {
		ArrayList<Insumo> insumos = new ArrayList<>();
		String sqlListarInsumosParados = "SELECT * FROM insumo WHERE empreendimento_id = ? AND updated_at < ? AND deleted_at IS NULL ORDER BY updated_at ASC";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sqlListarInsumosParados)) {
			LocalDate dataLimite = LocalDate.now().minusDays(diasSemMovimento);
			pstmt.setInt(1, empreendimentoId);
			pstmt.setString(2, dataLimite.format(DateTimeFormatter.ISO_LOCAL_DATE));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Insumo insumo = new Insumo();
				insumo.setId(rs.getInt("id"));
				insumo.setNome(rs.getString("nome"));
				insumo.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime()
						.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				insumos.add(insumo);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar insumos parados.", e);
		}
		return insumos;
	}

	public double calcularValorTotalEstoque(int empreendimentoId) {
		double valorTotal = 0;
		String sqlCalcularValorTotalEstoque = "SELECT SUM(i.quantidade * ic.preco_unitario) AS valor_total "
		           + "FROM insumo i "
		           + "JOIN ( "
		           + "    SELECT ci.insumo_id, ci.preco_unitario, "
		           + "           ROW_NUMBER() OVER(PARTITION BY ci.insumo_id ORDER BY c.created_at DESC) as rn "
		           + "    FROM compra_insumo ci "
		           + "    JOIN compra c ON ci.compra_id = c.id " 
		           + ") ic ON i.id = ic.insumo_id AND ic.rn = 1 "
		           + "WHERE i.empreendimento_id = ? AND i.deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sqlCalcularValorTotalEstoque)) {
			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				valorTotal = rs.getDouble("valor_total");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao calcular valor total do estoque.", e);
		}
		return valorTotal;
	}

	public ArrayList<Insumo> listarUltimosInsumosAdicionados(int empreendimentoId, int limite) {
		ArrayList<Insumo> insumos = new ArrayList<>();
		String sqlListarUltimosInsumosAdicionados = "SELECT * FROM insumo WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ?";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sqlListarUltimosInsumosAdicionados)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Insumo insumo = new Insumo();
				insumo.setId(rs.getInt("id"));
				insumo.setNome(rs.getString("nome"));
				insumo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()
						.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				insumos.add(insumo);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar últimos insumos adicionados.", e);
		}
		return insumos;
	}
	

}