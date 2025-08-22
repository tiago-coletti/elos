package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Compra;
import model.entity.CompraInsumo;
import model.entity.Insumo;
import util.ConnectionFactory;

public class CompraDAO {
	private static final Logger logger = Logger.getLogger(CompraDAO.class.getName());

	/**
	 * Registra uma compra, incluindo o pedido e os item.
	 * 
	 * @param compra         Compra que referencia a compra realizada pelo aluno.
	 * @param dataCompra     Data em que a compra foi realizada.
	 * @param compraitem     Lista de insumos que fazem parte da compra.
	 */
	public void registrarCompra(Compra compra, String dataCompra, List<CompraInsumo> compraInsumo) throws SQLException {

	    String sqlCompras = "INSERT INTO compra (data_compra, valor_total, empreendimento_id) VALUES (?, ?, ?)";
	    String sqlCompraInsumo = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
	    String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo - ? WHERE id = ?";

	    Connection con = null; 
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false);

	        // 1. Registrar a compra e obter o ID gerado
	        int compraId;
	        try (PreparedStatement pstmtCompra = con.prepareStatement(sqlCompras, Statement.RETURN_GENERATED_KEYS)) {
	            pstmtCompra.setDate(1, Date.valueOf(dataCompra));
	            pstmtCompra.setDouble(2, compra.getValorTotal());
	            pstmtCompra.setInt(3, compra.getEmpreendimentoId());
	            pstmtCompra.executeUpdate();

	            try (ResultSet rs = pstmtCompra.getGeneratedKeys()) {
	                if (rs.next()) {
	                    compraId = rs.getInt(1);
	                } else {
	                    throw new SQLException("Falha ao obter ID da compra, nenhuma chave gerada.");
	                }
	            }
	        }

	        // 2. Registrar insumos da compra em batch
	        try (PreparedStatement pstmtCompraInsumo = con.prepareStatement(sqlCompraInsumo)) {
	            for (CompraInsumo insumo : compraInsumo) {
	                pstmtCompraInsumo.setDouble(1, insumo.getPrecoUnitario());
	                pstmtCompraInsumo.setDouble(2, insumo.getQuantidadeComprada());
	                pstmtCompraInsumo.setDouble(3, insumo.getQuantidadeRestante());
	                pstmtCompraInsumo.setInt(4, insumo.getInsumoId());
	                pstmtCompraInsumo.setInt(5, compraId);
	                pstmtCompraInsumo.addBatch();
	            }
	            pstmtCompraInsumo.executeBatch();
	        }

	        // 3. Atualizar saldo do empreendimento
	        try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
	            pstmtSaldo.setDouble(1, compra.getValorTotal());
	            pstmtSaldo.setInt(2, compra.getEmpreendimentoId());
	            pstmtSaldo.executeUpdate();
	        }

	        con.commit();

	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao registrar a compra com insumo. Empreendimento ID: " + compra.getEmpreendimentoId(), e);
	        if (con != null) {
	            try {
	                logger.info("Realizando rollback da transação.");
	                con.rollback();
	            } catch (SQLException rollbackEx) {
	                logger.log(Level.SEVERE, "Erro CRÍTICO ao realizar o rollback.", rollbackEx);
	            }
	        }
	        throw e;
	    } finally {
	        if (con != null) {
	            try {
	                con.close();
	            } catch (SQLException closeEx) {
	                logger.log(Level.WARNING, "Erro ao fechar a conexão.", closeEx);
	            }
	        }
	    }
	}
	
	/**
	 * Recupera todos as compras da tabela "compra" de determinado empreendimento.
	 * 
	 * @return Lista de compras cadastradas no banco de dados.
	 */
	public ArrayList<Compra> listarCompras(int empreendimentoId) {
		ArrayList<Compra> compras = new ArrayList<>();
		String sqlListarCompras = "SELECT * FROM compra WHERE empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlListarCompras)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Compra compra = new Compra();
				compra.setId(rs.getInt("id"));
				compra.setDataCompra(rs.getString("data_compra"));
				compra.setCreatedAt(rs.getString("created_at"));
				compra.setValorTotal(rs.getDouble("valor_total"));

				compras.add(compra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar insumos.", e);
		}
		return compras;
	}
}