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

import model.entity.CompraInsumo;
import util.ConnectionFactory;

public class CompraInsumoDAO {
	
	private static final Logger logger = Logger.getLogger(CompraInsumoDAO.class.getName());

	/**
	 * Lista todos os lotes de um insumo específico que ainda possuem estoque.
	 * A lista é ordenada pela data de compra (FIFO).
	 * * @param insumoId O ID do insumo a ser consultado.
	 * @param empreendimentoId O ID do empreendimento para filtrar os lotes.
	 * @return Uma ArrayList de objetos CompraInsumo (lotes) com estoque.
	 */
	public ArrayList<CompraInsumo> listarLotesPorInsumoId(int insumoId, int empreendimentoId) {
		ArrayList<CompraInsumo> lotes = new ArrayList<>();
		String sql = "SELECT ci.id, ci.compra_id, ci.insumo_id, ci.preco_unitario, ci.quantidade_comprada, ci.quantidade_restante, c.data_compra "
				   + "FROM compra_insumo ci "
				   + "JOIN compra c ON ci.compra_id = c.id "
				   + "WHERE ci.insumo_id = ? AND c.empreendimento_id = ? AND ci.quantidade_restante > 0 "
				   + "ORDER BY c.data_compra ASC";

		try (Connection con = ConnectionFactory.conectar();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setInt(1, insumoId);
			pstmt.setInt(2, empreendimentoId);
			
			try(ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					CompraInsumo lote = new CompraInsumo();
					lote.setId(rs.getInt("id"));
					lote.setCompraId(rs.getInt("compra_id"));
					lote.setInsumoId(rs.getInt("insumo_id"));
					lote.setPrecoUnitario(rs.getDouble("preco_unitario"));
					lote.setQuantidadeComprada(rs.getDouble("quantidade_comprada"));
					lote.setQuantidadeRestante(rs.getDouble("quantidade_restante"));
					
					// Formata a data para String "dd/MM/yyyy"
					LocalDate data = rs.getDate("data_compra").toLocalDate();
					lote.setDataCompra(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
					
					lotes.add(lote);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar lotes de insumo para o ID: " + insumoId, e);
		}
		return lotes;
	}
	
	/**
	 * Obtém o custo unitário de um lote (CompraInsumo) específico.
	 * * @param compraInsumoId O ID do lote (compra_insumo.id).
	 * @param empreendimentoId O ID do empreendimento para verificação de permissão.
	 * @return O valor (double) do preco_unitario. Retorna -1 se não for encontrado.
	 */
	public double obterCustoLotePorId(int compraInsumoId, int empreendimentoId) {
		String sql = "SELECT ci.preco_unitario FROM compra_insumo ci "
				   + "JOIN compra c ON ci.compra_id = c.id "
				   + "WHERE ci.id = ? AND c.empreendimento_id = ?";
		
		try (Connection con = ConnectionFactory.conectar();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {
			
			pstmt.setInt(1, compraInsumoId);
			pstmt.setInt(2, empreendimentoId);
			
			try(ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("preco_unitario");
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao obter custo do lote ID: " + compraInsumoId, e);
		}
		return -1; 
	}

	/**
	 * Ajusta o estoque de um lote (Perda ou Retirada) de forma transacional.
	 * Se o tipo for "retirada", atualiza o saldo do empreendimento.
	 * * @param compraInsumoId O ID do lote (compra_insumo.id) a ser ajustado.
	 * @param empreendimentoId O ID do empreendimento.
	 * @param quantidadeAjuste A quantidade (positiva) a ser subtraída.
	 * @param tipoAjuste A razão do ajuste ("perda" ou "retirada").
	 * @param custoUnitario O custo do insumo naquele lote, para calcular a devolução ao saldo.
	 * @return true se a transação foi bem-sucedida.
	 * @throws SQLException Se ocorrer um erro no banco ou a validação (estoque) falhar.
	 */
	public boolean ajustarEstoque(int compraInsumoId, int empreendimentoId, double quantidadeAjuste, String tipoAjuste, double custoUnitario) throws SQLException {
		String sqlUpdateLote = "UPDATE compra_insumo SET quantidade_restante = quantidade_restante - ? "
							 + "WHERE id = ? AND quantidade_restante >= ?";
		
		String sqlUpdateSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";
		
		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false); 

			// 1. Subtrai do lote de compra_insumo
			try (PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdateLote)) {
				pstmtUpdate.setDouble(1, quantidadeAjuste);
				pstmtUpdate.setInt(2, compraInsumoId);
				pstmtUpdate.setDouble(3, quantidadeAjuste); 
				
				int rowsAffected = pstmtUpdate.executeUpdate();
				if (rowsAffected == 0) {
					// Não atualizou, provavelmente por falta de estoque (quantidadeAjuste > quantidade_restante)
					throw new SQLException("Estoque insuficiente no lote ou lote não encontrado.");
				}
			}

			// 2. Se for "Retirada", ajusta o saldo geral
			if ("retirada".equals(tipoAjuste)) {
				double valorRetirado = custoUnitario * quantidadeAjuste;
				
				try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlUpdateSaldo)) {
					pstmtSaldo.setDouble(1, valorRetirado);
					pstmtSaldo.setInt(2, empreendimentoId);
					
					int saldoRowsAffected = pstmtSaldo.executeUpdate();
					if (saldoRowsAffected == 0) {
						throw new SQLException("Falha ao atualizar saldo: Empreendimento não encontrado.");
					}
				}
			}

			con.commit(); 
			return true;

		} catch (SQLException e) {
			if (con != null) {
				con.rollback(); 
			}
			logger.log(Level.SEVERE, "Erro na transação de ajuste de estoque.", e);
			throw e; 
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					logger.log(Level.SEVERE, "Erro ao fechar conexão pós-transação.", e);
				}
			}
		}
	}
}