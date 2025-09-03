package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Compra;
import model.entity.CompraInsumo;
import util.ConnectionFactory;

public class CompraDAO {
	private static final Logger logger = Logger.getLogger(CompraDAO.class.getName());

	/**
	 * Registra uma compra, incluindo o pedido, os itens E ATUALIZA O ESTOQUE.
	 * @param compra         Compra que referencia a compra realizada pelo aluno.
	 * @param compraInsumo     Lista de insumos que fazem parte da compra.
	 */
	public void registrarCompra(Compra compra, List<CompraInsumo> compraInsumo) throws SQLException {
		
	    String sqlCompras = "INSERT INTO compra (data_compra, valor_total, empreendimento_id) VALUES (?, ?, ?)";
	    String sqlCompraInsumo = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
	    String sqlAtualizarEstoqueInsumo = "UPDATE insumo SET quantidade = COALESCE(quantidade, 0) + ? WHERE id = ?";
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

	        // 3. Atualizar a quantidade de cada insumo no estoque 
	        try (PreparedStatement pstmtAtualizarInsumo = con.prepareStatement(sqlAtualizarEstoqueInsumo)) {
	            for (CompraInsumo insumo : compraInsumo) {
	                pstmtAtualizarInsumo.setDouble(1, insumo.getQuantidadeComprada()); 
	                pstmtAtualizarInsumo.setInt(2, insumo.getInsumoId());
	                pstmtAtualizarInsumo.addBatch();
	            }
	            pstmtAtualizarInsumo.executeBatch();
	        }

	        // 4. Atualizar saldo do empreendimento
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
	 * substituindo os itens antigos pelos novos, ajustando o saldo do empreendimento E O ESTOQUE DE INSUMOS.
	 *
	 * @param compra       O objeto Compra contendo os DADOS NOVOS e o ID da compra a ser editada.
	 * @param novosInsumos A nova lista de insumos que irão compor a compra.
	 */
	public void editarCompra(Compra compra, List<CompraInsumo> novosInsumos) throws SQLException {

	    // SQLs para buscar dados antigos e para atualizar a compra
	    String sqlBuscarValorAntigo = "SELECT valor_total FROM compra WHERE id = ?";
	    String sqlBuscarInsumosAntigos = "SELECT insumo_id, quantidade_comprada FROM compra_insumo WHERE compra_id = ?";
	    String sqlUpdateCompra = "UPDATE compra SET data_compra = ?, valor_total = ? WHERE id = ?";
	    
	    // SQLs para manipulação de itens e saldo
	    String sqlDeleteInsumosAntigos = "DELETE FROM compra_insumo WHERE compra_id = ?";
	    String sqlInsertNovosInsumos = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
	    String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? - ? WHERE id = ?"; 
	    String sqlAjustarEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?";

	    Connection con = null;
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false); 

	        double valorAntigo = 0;
	        Map<Integer, Double> mapaQuantidadesAntigas = new HashMap<>();

	        // 1. Buscar valor total antigo da compra
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

	        // 2. Buscar quantidades antigas dos insumos e guardar em um mapa 
	        try (PreparedStatement pstmtBuscaInsumos = con.prepareStatement(sqlBuscarInsumosAntigos)) {
	            pstmtBuscaInsumos.setInt(1, compra.getId());
	            try (ResultSet rs = pstmtBuscaInsumos.executeQuery()) {
	                while (rs.next()) {
	                    mapaQuantidadesAntigas.put(rs.getInt("insumo_id"), rs.getDouble("quantidade_comprada"));
	                }
	            }
	        }

	        // 3. Calcular a diferença de estoque e aplicar o ajuste ---
	        try (PreparedStatement pstmtAjustarEstoque = con.prepareStatement(sqlAjustarEstoqueInsumo)) {
	            Map<Integer, Double> mapaQuantidadesNovas = new HashMap<>();
	            Set<Integer> todosInsumosIds = new HashSet<>(mapaQuantidadesAntigas.keySet());

	            // Mapeia novas quantidades e coleta todos os IDs de insumos envolvidos
	            for (CompraInsumo insumo : novosInsumos) {
	                mapaQuantidadesNovas.put(insumo.getInsumoId(), insumo.getQuantidadeComprada());
	                todosInsumosIds.add(insumo.getInsumoId());
	            }

	            // Para cada insumo, calcula a diferença e adiciona ao batch de update
	            for (Integer insumoId : todosInsumosIds) {
	                double quantidadeAntiga = mapaQuantidadesAntigas.getOrDefault(insumoId, 0.0);
	                double quantidadeNova = mapaQuantidadesNovas.getOrDefault(insumoId, 0.0);
	                double diferenca = quantidadeNova - quantidadeAntiga;

	                if (diferenca != 0) {
	                    pstmtAjustarEstoque.setDouble(1, diferenca);
	                    pstmtAjustarEstoque.setInt(2, insumoId);
	                    pstmtAjustarEstoque.addBatch();
	                }
	            }
	            pstmtAjustarEstoque.executeBatch();
	        }

	        // 4. Atualizar os dados da compra (data e novo valor)
	        try (PreparedStatement pstmtUpdateCompra = con.prepareStatement(sqlUpdateCompra)) {
	            pstmtUpdateCompra.setDate(1, Date.valueOf(compra.getDataCompra()));
	            pstmtUpdateCompra.setDouble(2, compra.getValorTotal());
	            pstmtUpdateCompra.setInt(3, compra.getId());
	            pstmtUpdateCompra.executeUpdate();
	        }

	        // 5. Deletar os insumos antigos da compra
	        try (PreparedStatement pstmtDeleteInsumos = con.prepareStatement(sqlDeleteInsumosAntigos)) {
	            pstmtDeleteInsumos.setInt(1, compra.getId());
	            pstmtDeleteInsumos.executeUpdate();
	        }

	        // 6. Inserir os novos insumos
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

	        // 7. Ajustar o saldo do empreendimento
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
	 * Realiza a exclusão lógica (soft delete) de uma compra, revertendo o valor ao saldo do empreendimento
	 * e subtraindo as quantidades do estoque de insumos.
	 *
	 * @param compraId         ID da compra a ser marcada como excluída.
	 * @param empreendimentoId ID do empreendimento para verificação de propriedade.
	 * @return                 true se a exclusão completa foi bem-sucedida, false caso contrário.
	 */
	public boolean excluirCompra(int compraId, int empreendimentoId) {
	    // SQLs para buscar os dados necessários para a reversão
	    String sqlBuscarCompra = "SELECT valor_total FROM compra WHERE id = ? AND empreendimento_id = ?";
	    String sqlBuscarInsumosDaCompra = "SELECT insumo_id, quantidade_comprada FROM compra_insumo WHERE compra_id = ?";

	    // SQLs para executar as operações de reversão e exclusão
	    String sqlReverterEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade - ? WHERE id = ?";
	    String sqlReverterSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";
	    String sqlExcluirCompra = "UPDATE compra SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";

	    Connection con = null;
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false); 

	        double valorTotalCompra = 0;
	        List<CompraInsumo> insumosParaReverter = new ArrayList<>();

	        // 1. Buscar o valor total da compra a ser excluída
	        try (PreparedStatement pstmtBuscaCompra = con.prepareStatement(sqlBuscarCompra)) {
	            pstmtBuscaCompra.setInt(1, compraId);
	            pstmtBuscaCompra.setInt(2, empreendimentoId);
	            try (ResultSet rs = pstmtBuscaCompra.executeQuery()) {
	                if (rs.next()) {
	                    valorTotalCompra = rs.getDouble("valor_total");
	                } else {
	                    throw new SQLException("Compra com ID " + compraId + " não encontrada ou não pertence ao empreendimento.");
	                }
	            }
	        }

	        // 2. Buscar todos os insumos e quantidades da compra
	        try (PreparedStatement pstmtBuscaInsumos = con.prepareStatement(sqlBuscarInsumosDaCompra)) {
	            pstmtBuscaInsumos.setInt(1, compraId);
	            try (ResultSet rs = pstmtBuscaInsumos.executeQuery()) {
	                while (rs.next()) {
	                    CompraInsumo item = new CompraInsumo();
	                    item.setInsumoId(rs.getInt("insumo_id"));
	                    item.setQuantidadeComprada(rs.getDouble("quantidade_comprada"));
	                    insumosParaReverter.add(item);
	                }
	            }
	        }
	        
	        // 3. Reverter o estoque (subtrair as quantidades compradas)
	        if (!insumosParaReverter.isEmpty()) {
	            try (PreparedStatement pstmtReverteEstoque = con.prepareStatement(sqlReverterEstoqueInsumo)) {
	                for (CompraInsumo item : insumosParaReverter) {
	                    pstmtReverteEstoque.setDouble(1, item.getQuantidadeComprada());
	                    pstmtReverteEstoque.setInt(2, item.getInsumoId());
	                    pstmtReverteEstoque.addBatch();
	                }
	                pstmtReverteEstoque.executeBatch();
	            }
	        }

	        // 4. Reverter o saldo (devolver o valor da compra ao empreendimento)
	        try (PreparedStatement pstmtReverteSaldo = con.prepareStatement(sqlReverterSaldo)) {
	            pstmtReverteSaldo.setDouble(1, valorTotalCompra);
	            pstmtReverteSaldo.setInt(2, empreendimentoId);
	            pstmtReverteSaldo.executeUpdate();
	        }

	        // 5. Marcar a compra como excluída (soft delete)
	        try (PreparedStatement pstmtExcluir = con.prepareStatement(sqlExcluirCompra)) {
	            pstmtExcluir.setInt(1, compraId);
	            pstmtExcluir.setInt(2, empreendimentoId);
	            int rowsAffected = pstmtExcluir.executeUpdate();
	            if (rowsAffected == 0) {
	            	// Se nada foi afetado aqui, a compra não existe, então falha a transação.
	                throw new SQLException("A exclusão lógica da compra falhou, nenhuma linha afetada.");
	            }
	        }

	        con.commit(); 
	        return true;

	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao realizar exclusão da compra com ID: " + compraId + ". Realizando rollback.", e);
	        if (con != null) {
	            try {
	                con.rollback(); 
	            } catch (SQLException rollbackEx) {
	                logger.log(Level.SEVERE, "Erro CRÍTICO ao realizar o rollback da exclusão.", rollbackEx);
	            }
	        }
	        return false;
	    } finally {
	        if (con != null) {
	            try {
	                con.close();
	            } catch (SQLException closeEx) {
	                logger.log(Level.WARNING, "Erro ao fechar a conexão após tentativa de exclusão.", closeEx);
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