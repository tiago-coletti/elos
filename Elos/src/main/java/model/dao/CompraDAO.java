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

	public void registrarCompra(Compra compra, List<CompraInsumo> compraInsumo) throws SQLException {
		
		String sqlCompras = "INSERT INTO compra (data_compra, valor_total, empreendimento_id) VALUES (?, ?, ?)";
		String sqlCompraInsumo = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
		String sqlAtualizarEstoqueInsumo = "UPDATE insumo SET quantidade = COALESCE(quantidade, 0) + ? WHERE id = ?";
		String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo - ? WHERE id = ?";

		Connection con = null;	
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);

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

			try (PreparedStatement pstmtAtualizarInsumo = con.prepareStatement(sqlAtualizarEstoqueInsumo)) {
				for (CompraInsumo insumo : compraInsumo) {
					pstmtAtualizarInsumo.setDouble(1, insumo.getQuantidadeComprada());	
					pstmtAtualizarInsumo.setInt(2, insumo.getInsumoId());
					pstmtAtualizarInsumo.addBatch();
				}
				pstmtAtualizarInsumo.executeBatch();
			}

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


	public void editarCompra(Compra compra, List<CompraInsumo> novosInsumos) throws SQLException {

		String sqlBuscarValorAntigo = "SELECT valor_total FROM compra WHERE id = ?";
		String sqlBuscarInsumosAntigos = "SELECT insumo_id, quantidade_comprada, quantidade_restante FROM compra_insumo WHERE compra_id = ?";
		String sqlUpdateCompra = "UPDATE compra SET data_compra = ?, valor_total = ? WHERE id = ?";
		
		String sqlDeleteInsumosAntigos = "DELETE FROM compra_insumo WHERE compra_id = ?";
		String sqlInsertNovosInsumos = "INSERT INTO compra_insumo (preco_unitario, quantidade_comprada, quantidade_restante, insumo_id, compra_id) VALUES (?, ?, ?, ?, ?)";
		String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? - ? WHERE id = ?";	
		String sqlAjustarEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?";

		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);	

			double valorAntigo = 0;
			Map<Integer, Double> mapaQuantidadesCompradasAntigas = new HashMap<>();
			Map<Integer, Double> mapaQuantidadesConsumidasAntigas = new HashMap<>();

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

			try (PreparedStatement pstmtBuscaInsumos = con.prepareStatement(sqlBuscarInsumosAntigos)) {
				pstmtBuscaInsumos.setInt(1, compra.getId());
				try (ResultSet rs = pstmtBuscaInsumos.executeQuery()) {
					while (rs.next()) {
						int insumoId = rs.getInt("insumo_id");
						double qntComprada = rs.getDouble("quantidade_comprada");
						double qntRestante = rs.getDouble("quantidade_restante");
						mapaQuantidadesCompradasAntigas.put(insumoId, qntComprada);
						mapaQuantidadesConsumidasAntigas.put(insumoId, qntComprada - qntRestante);
					}
				}
			}

			try (PreparedStatement pstmtAjustarEstoque = con.prepareStatement(sqlAjustarEstoqueInsumo)) {
				Map<Integer, Double> mapaQuantidadesNovas = new HashMap<>();
				Set<Integer> todosInsumosIds = new HashSet<>(mapaQuantidadesCompradasAntigas.keySet());

				for (CompraInsumo insumo : novosInsumos) {
					mapaQuantidadesNovas.put(insumo.getInsumoId(), insumo.getQuantidadeComprada());
					todosInsumosIds.add(insumo.getInsumoId());
				}

				for (Integer insumoId : todosInsumosIds) {
					double quantidadeAntiga = mapaQuantidadesCompradasAntigas.getOrDefault(insumoId, 0.0);
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

			try (PreparedStatement pstmtUpdateCompra = con.prepareStatement(sqlUpdateCompra)) {
				pstmtUpdateCompra.setDate(1, Date.valueOf(compra.getDataCompra()));
				pstmtUpdateCompra.setDouble(2, compra.getValorTotal());
				pstmtUpdateCompra.setInt(3, compra.getId());
				pstmtUpdateCompra.executeUpdate();
			}

			try (PreparedStatement pstmtDeleteInsumos = con.prepareStatement(sqlDeleteInsumosAntigos)) {
				pstmtDeleteInsumos.setInt(1, compra.getId());
				pstmtDeleteInsumos.executeUpdate();
			}

			try (PreparedStatement pstmtInsertInsumos = con.prepareStatement(sqlInsertNovosInsumos)) {
				for (CompraInsumo insumo : novosInsumos) {
					int insumoId = insumo.getInsumoId();
					double quantidadeNova = insumo.getQuantidadeComprada();
					double consumidoAntigo = mapaQuantidadesConsumidasAntigas.getOrDefault(insumoId, 0.0);

					if (quantidadeNova < consumidoAntigo) {
						throw new SQLException("Edição não permitida: A nova quantidade (" + quantidadeNova + 
								") é menor do que a já consumida (" + consumidoAntigo + ") para o insumo ID " + insumoId);
					}
					
					double novaQuantidadeRestante = quantidadeNova - consumidoAntigo;
					
					pstmtInsertInsumos.setDouble(1, insumo.getPrecoUnitario());
					pstmtInsertInsumos.setDouble(2, quantidadeNova);
					pstmtInsertInsumos.setDouble(3, novaQuantidadeRestante);
					pstmtInsertInsumos.setInt(4, insumoId);
					pstmtInsertInsumos.setInt(5, compra.getId());
					pstmtInsertInsumos.addBatch();
				}
				pstmtInsertInsumos.executeBatch();
			}

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
	
	public boolean excluirCompra(int compraId, int empreendimentoId) {
		String sqlBuscarCompra = "SELECT valor_total FROM compra WHERE id = ? AND empreendimento_id = ?";
		String sqlBuscarInsumosDaCompra = "SELECT insumo_id, quantidade_comprada, quantidade_restante FROM compra_insumo WHERE compra_id = ?";

		String sqlReverterEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade - ? WHERE id = ?";
		String sqlDeleteInsumos = "DELETE FROM compra_insumo WHERE compra_id = ?";
		String sqlReverterSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";
		String sqlExcluirCompra = "UPDATE compra SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";

		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);	

			double valorTotalCompra = 0;
			Map<Integer, Double> insumosParaReverter = new HashMap<>();

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

			try (PreparedStatement pstmtBuscaInsumos = con.prepareStatement(sqlBuscarInsumosDaCompra)) {
				pstmtBuscaInsumos.setInt(1, compraId);
				try (ResultSet rs = pstmtBuscaInsumos.executeQuery()) {
					while (rs.next()) {
						double qntComprada = rs.getDouble("quantidade_comprada");
						double qntRestante = rs.getDouble("quantidade_restante");
						
						if (qntRestante < qntComprada) {
							throw new SQLException("Exclusão não permitida: O lote de insumo ID " + rs.getInt("insumo_id") + 
									" (Compra ID: " + compraId + ") já foi parcialmente consumido em uma venda.");
						}
						
						insumosParaReverter.put(rs.getInt("insumo_id"), qntComprada);
					}
				}
			}
			
			if (!insumosParaReverter.isEmpty()) {
				try (PreparedStatement pstmtReverteEstoque = con.prepareStatement(sqlReverterEstoqueInsumo)) {
					for (Map.Entry<Integer, Double> entry : insumosParaReverter.entrySet()) {
						pstmtReverteEstoque.setDouble(1, entry.getValue());
						pstmtReverteEstoque.setInt(2, entry.getKey());
						pstmtReverteEstoque.addBatch();
					}
					pstmtReverteEstoque.executeBatch();
				}
				
				try (PreparedStatement pstmtDeleteInsumos = con.prepareStatement(sqlDeleteInsumos)) {
					pstmtDeleteInsumos.setInt(1, compraId);
					pstmtDeleteInsumos.executeUpdate();
				}
			}

			try (PreparedStatement pstmtReverteSaldo = con.prepareStatement(sqlReverterSaldo)) {
				pstmtReverteSaldo.setDouble(1, valorTotalCompra);
				pstmtReverteSaldo.setInt(2, empreendimentoId);
				pstmtReverteSaldo.executeUpdate();
			}

			try (PreparedStatement pstmtExcluir = con.prepareStatement(sqlExcluirCompra)) {
				pstmtExcluir.setInt(1, compraId);
				pstmtExcluir.setInt(2, empreendimentoId);
				int rowsAffected = pstmtExcluir.executeUpdate();
				if (rowsAffected == 0) {
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
	
	private List<CompraInsumo> listarInsumosPorCompraId(int compraId, Connection con) throws SQLException {
		List<CompraInsumo> insumos = new ArrayList<>();
		
		String sqlListarInsumosPorCompraId = "SELECT ci.*, i.nome AS insumo_nome " +
											 "FROM compra_insumo ci " +
											 "JOIN insumo i ON ci.insumo_id = i.id " +
											 "WHERE ci.compra_id = ?";

		try (PreparedStatement pstmt = con.prepareStatement(sqlListarInsumosPorCompraId)) {
			pstmt.setInt(1, compraId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					CompraInsumo insumo = new CompraInsumo();
					insumo.setId(rs.getInt("id"));
					insumo.setPrecoUnitario(rs.getDouble("preco_unitario"));
					insumo.setQuantidadeComprada(rs.getDouble("quantidade_comprada"));
					insumo.setQuantidadeRestante(rs.getDouble("quantidade_restante"));
					insumo.setInsumoId(rs.getInt("insumo_id"));
					insumo.setCompraId(rs.getInt("compra_id"));
					
					insumo.setInsumoNome(rs.getString("insumo_nome"));	
					insumos.add(insumo);
				}
			}
		}
		return insumos;
	}
	
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

					List<CompraInsumo> insumosDaCompra = listarInsumosPorCompraId(compra.getId(), con);
					compra.setInsumos(insumosDaCompra);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao obter compra com ID: " + id, e);
		}

		return compra;
	}
	
	public double calcularTotalGastoPorPeriodo(int empreendimentoId, String periodo) {
		double total = 0.0;
		String sql = "SELECT SUM(valor_total) AS total FROM compra WHERE empreendimento_id = ? AND deleted_at IS NULL AND data_compra >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				total = rs.getDouble("total");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao calcular total gasto.", e);
		}
		return total;
	}

	public ArrayList<Compra> listarUltimasCompras(int empreendimentoId, int limite) {
		ArrayList<Compra> compras = new ArrayList<>();
		String sql = "SELECT * FROM compra WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY data_compra DESC, id DESC LIMIT ?";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Compra compra = new Compra();
				compra.setId(rs.getInt("id"));
				compra.setDataCompra(rs.getString("data_compra"));
				compra.setValorTotal(rs.getDouble("valor_total"));
				
				compra.setInsumos(listarInsumosPorCompraId(compra.getId(), con));
				compras.add(compra);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar últimas compras.", e);
		}
		return compras;
	}
	
	public ArrayList<Map<String, Object>> analisarVariacaoPrecos(int empreendimentoId, int limite) {
		ArrayList<Map<String, Object>> variacoes = new ArrayList<>();
		String sql = "WITH PrecosRanqueados AS (" +
					 "	 SELECT " +
					 "		 i.nome AS insumo_nome, " +
					 "		 ci.preco_unitario, " +
					 "		 c.data_compra, " +
					 "		 ROW_NUMBER() OVER(PARTITION BY ci.insumo_id ORDER BY c.data_compra DESC) as rn " +
					 "	 FROM compra_insumo ci " +
					 "	 JOIN compra c ON ci.compra_id = c.id " +
					 "	 JOIN insumo i ON ci.insumo_id = i.id " +
					 "	 WHERE c.empreendimento_id = ? AND c.deleted_at IS NULL" +
					 ") " +
					 "SELECT " +
					 "	 p1.insumo_nome, " +
					 "	 p1.preco_unitario AS preco_atual, " +
					 "	 p2.preco_unitario AS preco_anterior, " +
					 "	 ((p1.preco_unitario - p2.preco_unitario) / p2.preco_unitario) * 100 AS variacao_percentual " +
					 "FROM PrecosRanqueados p1 " +
					 "LEFT JOIN PrecosRanqueados p2 ON p1.insumo_nome = p2.insumo_nome AND p2.rn = 2 " +
					 "WHERE p1.rn = 1 AND p2.preco_unitario IS NOT NULL AND p2.preco_unitario > 0 " +
					 "ORDER BY ABS(variacao_percentual) DESC " +
					 "LIMIT ?";

		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> variacao = new HashMap<>();
				variacao.put("nome", rs.getString("insumo_nome"));
				variacao.put("variacao", rs.getDouble("variacao_percentual"));
				variacoes.add(variacao);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao analisar variação de preços.", e);
		}
		return variacoes;
	}

	public ArrayList<Map<String, Object>> listarMaioresDespesas(int empreendimentoId, String periodo, int limite) {
		ArrayList<Map<String, Object>> despesas = new ArrayList<>();
		String sql = "SELECT i.nome, SUM(ci.preco_unitario * ci.quantidade_comprada) AS total_gasto " +
					 "FROM compra_insumo ci " +
					 "JOIN insumo i ON ci.insumo_id = i.id " +
					 "JOIN compra c ON ci.compra_id = c.id " +
					 "WHERE c.empreendimento_id = ? AND c.deleted_at IS NULL AND c.data_compra >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
					 "GROUP BY i.nome " +
					 "ORDER BY total_gasto DESC " +
					 "LIMIT ?";
					 
		try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setInt(1, empreendimentoId);
			pstmt.setInt(2, limite);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> despesa = new HashMap<>();
				despesa.put("nome", rs.getString("nome"));
				despesa.put("total", rs.getDouble("total_gasto"));
				despesas.add(despesa);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar maiores despesas.", e);
		}
		return despesas;
	}
}