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
	public void registrarCompra(Compra compra, List<CompraInsumo> compraInsumo) throws SQLException {

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
	            pstmtCompra.setDate(1, Date.valueOf(compra.getDataCompra()));
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
	 * Edita uma compra existente, atualizando seus dados, recalculando o valor total,
	 * substituindo os itens antigos pelos novos e ajustando o saldo do empreendimento.
	 *
	 * @param compra       O objeto Compra contendo os DADOS NOVOS e o ID da compra a ser editada.
	 * @param novosInsumos A nova lista de insumos que irão compor a compra.
	 */
	public void editarCompra(Compra compra, List<CompraInsumo> novosInsumos) throws SQLException {

		String sqlBuscarValorAntigo = "SELECT valor_total FROM compra WHERE id = ?";
	    String sqlUpdateCompra = "UPDATE compra SET data_compra = ?, valor_total = ? WHERE id = ?";
	    String sqlDeleteInsumosAntigos = "DELETE FROM compra_insumo WHERE compra_id = ?";
	    String sqlInsertNovosInsumos = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
	    String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? - ? WHERE id = ?"; 

	    Connection con = null;
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false); 

	        double valorAntigo = 0;

	        // 1. Buscar o valor total antigo da compra para o ajuste de saldo
	        try (PreparedStatement pstmtBusca = con.prepareStatement(sqlBuscarValorAntigo)) {
	            pstmtBusca.setInt(1, compra.getId());
	            try (ResultSet rs = pstmtBusca.executeQuery()) {
	                if (rs.next()) {
	                    valorAntigo = rs.getDouble("valor_total");
	                } else {
	                    throw new SQLException("Erro na edição: Compra com ID " + compra.getId() + " não encontrada.");
	                }
	            }
	        }

	        // 2. Atualizar os dados principais da compra (data e novo valor total)
	        try (PreparedStatement pstmtUpdateCompra = con.prepareStatement(sqlUpdateCompra)) {
	            pstmtUpdateCompra.setDate(1, Date.valueOf(compra.getDataCompra()));
	            pstmtUpdateCompra.setDouble(2, compra.getValorTotal());
	            pstmtUpdateCompra.setInt(3, compra.getId());
	            pstmtUpdateCompra.executeUpdate();
	        }

	        // 3. Deletar todos os insumos antigos associados a esta compra
	        try (PreparedStatement pstmtDeleteInsumos = con.prepareStatement(sqlDeleteInsumosAntigos)) {
	            pstmtDeleteInsumos.setInt(1, compra.getId());
	            pstmtDeleteInsumos.executeUpdate();
	        }

	        // 4. Inserir os novos insumos da compra em batch
	        try (PreparedStatement pstmtInsertInsumos = con.prepareStatement(sqlInsertNovosInsumos)) {
	            for (CompraInsumo insumo : novosInsumos) {
	                pstmtInsertInsumos.setDouble(1, insumo.getPrecoUnitario());
	                pstmtInsertInsumos.setDouble(2, insumo.getQuantidadeComprada());
	                pstmtInsertInsumos.setDouble(3, insumo.getQuantidadeRestante());
	                pstmtInsertInsumos.setInt(4, insumo.getInsumoId());
	                pstmtInsertInsumos.setInt(5, compra.getId());
	                pstmtInsertInsumos.addBatch();
	            }
	            pstmtInsertInsumos.executeBatch();
	        }

	        // 5. Ajustar o saldo do empreendimento: Adiciona o valor antigo de volta e subtrai o novo valor
	        try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
	            pstmtSaldo.setDouble(1, valorAntigo);
	            pstmtSaldo.setDouble(2, compra.getValorTotal());
	            pstmtSaldo.setInt(3, compra.getEmpreendimentoId());
	            pstmtSaldo.executeUpdate();
	        }

	        con.commit(); 

	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao EDITAR a compra com insumo. Compra ID: " + compra.getId(), e);
	        if (con != null) {
	            try {
	                logger.info("Realizando rollback da transação de edição.");
	                con.rollback();
	            } catch (SQLException rollbackEx) {
	                logger.log(Level.SEVERE, "Erro CRÍTICO ao realizar o rollback da edição.", rollbackEx);
	            }
	        }
	        throw e;
	    } finally {
	        if (con != null) {
	            try {
	                con.close();
	            } catch (SQLException closeEx) {
	                logger.log(Level.WARNING, "Erro ao fechar a conexão após tentativa de edição.", closeEx);
	            }
	        }
	    }
	}
	
	/**
	 * Realiza a exclusão lógica (soft delete) de uma compra, marcando-a com a data e hora da exclusão.
	 *
	 * @param compraId         ID da compra a ser marcada como excluída.
	 * @param empreendimentoId ID do empreendimento para verificação de propriedade.
	 * @return                 true se a compra foi marcada como excluída com sucesso, false caso contrário.
	 */
	public boolean excluirCompra(int compraId, int empreendimentoId) {
	    String sqlExcluirCompra = "UPDATE compra SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";

	    try (Connection con = ConnectionFactory.conectar();
	         PreparedStatement pstmt = con.prepareStatement(sqlExcluirCompra)) {

	        pstmt.setInt(1, compraId);
	        pstmt.setInt(2, empreendimentoId);

	        int rowsAffected = pstmt.executeUpdate();

	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao realizar exclusão lógica da compra com ID: " + compraId, e);
	    }

	    return false;
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
	
	/**
	 * Método auxiliar para listar todos os insumos de uma compra específica.
	 *
	 * @param compraId O ID da compra da qual queremos os insumos.
	 * @param con A conexão de banco de dados existente para evitar criar uma nova.
	 * @return Uma lista de objetos CompraInsumo.
	 * @throws SQLException
	 */
	private List<CompraInsumo> listarInsumosPorCompraId(int compraId, Connection con) throws SQLException {
	    List<CompraInsumo> insumos = new ArrayList<>();
	    
	    // Esta query é a ideal: busca os dados da compra_insumo e o NOME do insumo da tabela 'insumo'
	    String sql = "SELECT ci.*, i.nome AS insumo_nome " +
	                 "FROM compra_insumo ci " +
	                 "JOIN insumo i ON ci.insumo_id = i.id " +
	                 "WHERE ci.compra_id = ?";

	    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
	        pstmt.setInt(1, compraId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                CompraInsumo item = new CompraInsumo();
	                item.setId(rs.getInt("id"));
	                item.setPrecoUnitario(rs.getDouble("preco_unitario"));
	                item.setQuantidadeComprada(rs.getDouble("quantidade_comprada"));
	                item.setInsumoId(rs.getInt("insumo_id"));
	                item.setCompraId(rs.getInt("compra_id"));
	                
	                item.setInsumoNome(rs.getString("insumo_nome")); 
	                insumos.add(item);
	            }
	        }
	    }
	    return insumos;
	}
	
	/**
	 * Recupera uma compra pelo seu ID, incluindo todos os seus insumos (itens) associados.
	 *
	 * @param id O ID da compra a ser recuperada.
	 * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
	 * @return Objeto Compra com os dados e a lista de itens preenchida, ou null se não for encontrada.
	 */
	public Compra obterCompraPorId(int id, int empreendimentoId) {
	    Compra compra = null;
	    String sqlObterCompra = "SELECT * FROM compra WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";

	    try (Connection con = ConnectionFactory.conectar();
	         PreparedStatement pstmt = con.prepareStatement(sqlObterCompra)) {

	        pstmt.setInt(1, id);
	        pstmt.setInt(2, empreendimentoId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                compra = new Compra();
	                compra.setId(rs.getInt("id"));
	                compra.setDataCompra(rs.getString("data_compra"));
	                compra.setValorTotal(rs.getDouble("valor_total"));
	                compra.setEmpreendimentoId(rs.getInt("empreendimento_id"));

	                List<CompraInsumo> itensDaCompra = listarInsumosPorCompraId(compra.getId(), con);
	                compra.setItens(itensDaCompra);
	            }
	        }
	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao obter compra com ID: " + id, e);
	    }

	    return compra;
	}
}