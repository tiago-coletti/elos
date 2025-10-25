package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import model.entity.Venda;
import model.entity.VendaProduto;
import util.ConnectionFactory;

public class VendaDAO {
	private static final Logger logger = Logger.getLogger(VendaDAO.class.getName());

	private static class LoteInsumo {
		int id;
		double quantidadeRestante;

		LoteInsumo(int id, double quantidadeRestante) {
			this.id = id;
			this.quantidadeRestante = quantidadeRestante;
		}
	}

	/**
	 * Calcula a quantidade total de cada insumo necessário para produzir os itens de uma venda.
	 * @param con              A conexão ativa com o banco de dados.
	 * @param produtosVendidos Um mapa com o ID do produto e a quantidade vendida.
	 * @return                 Um mapa consolidado com o ID do insumo e a quantidade total a ser deduzida.
	 * @throws SQLException   Lançada se ocorrer um erro na consulta ao banco de dados.
	 */
	private Map<Integer, Double> obterInsumosParaDeduzirDoEstoque(Connection con, Map<Integer, Double> produtosVendidos) throws SQLException {
		String sqlBuscarInsumos = "SELECT insumo_id, quantidade_utilizada FROM produto_insumo WHERE produto_id = ?";
		Map<Integer, Double> insumosADeduzir = new HashMap<>();
		if (produtosVendidos == null || produtosVendidos.isEmpty()) {
			return insumosADeduzir;
		}
		try (PreparedStatement pstmt = con.prepareStatement(sqlBuscarInsumos)) {
			for (Map.Entry<Integer, Double> produtoVendido : produtosVendidos.entrySet()) {
				pstmt.setInt(1, produtoVendido.getKey());
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						int insumoId = rs.getInt("insumo_id");
						double quantidadePorProduto = rs.getDouble("quantidade_utilizada");
						double quantidadeTotalInsumo = quantidadePorProduto * produtoVendido.getValue();
						insumosADeduzir.merge(insumoId, quantidadeTotalInsumo, Double::sum);
					}
				}
			}
		}
		return insumosADeduzir;
	}

	/**
	 * Deduz a quantidade de insumos do estoque, seguindo a lógica FIFO (First-In, First-Out) em cascata.
	 * Atualiza tanto a quantidade total do insumo quanto a quantidade restante nos lotes de compra.
	 * @param con               A conexão ativa com o banco de dados.
	 * @param insumosParaDeduzir Um mapa com o ID do insumo e a quantidade total a ser deduzida.
	 * @throws SQLException     Lançada se ocorrer um erro na atualização do banco de dados.
	 */
	private void deduzirInsumosDosLotesFIFO(Connection con, Map<Integer, Double> insumosParaDeduzir) throws SQLException {
		String sqlBuscarLotes = "SELECT ci.id, ci.quantidade_restante " +
								"FROM compra_insumo ci JOIN compra c ON ci.compra_id = c.id " +
								"WHERE ci.insumo_id = ? AND ci.quantidade_restante > 0 " +
								"ORDER BY c.data_compra ASC, c.id ASC";
		String sqlAtualizarLote = "UPDATE compra_insumo SET quantidade_restante = ? WHERE id = ?";
		String sqlAtualizarInsumoTotal = "UPDATE insumo SET quantidadeEmEstoque = quantidadeEmEstoque - ? WHERE id = ?";

		try (PreparedStatement pstmtBuscar = con.prepareStatement(sqlBuscarLotes);
			 PreparedStatement pstmtAtualizarLote = con.prepareStatement(sqlAtualizarLote);
			 PreparedStatement pstmtAtualizarTotal = con.prepareStatement(sqlAtualizarInsumoTotal)) {

			for (Map.Entry<Integer, Double> insumoEntry : insumosParaDeduzir.entrySet()) {
				int insumoId = insumoEntry.getKey();
				double quantidadeADeduzir = insumoEntry.getValue();
				
				pstmtAtualizarTotal.setDouble(1, quantidadeADeduzir);
				pstmtAtualizarTotal.setInt(2, insumoId);
				pstmtAtualizarTotal.addBatch();

				pstmtBuscar.setInt(1, insumoId);
				List<LoteInsumo> lotesDisponiveis = new ArrayList<>();
				try (ResultSet rs = pstmtBuscar.executeQuery()) {
					while (rs.next()) {
						lotesDisponiveis.add(new LoteInsumo(rs.getInt("id"), rs.getDouble("quantidade_restante")));
					}
				}

				for (LoteInsumo lote : lotesDisponiveis) {
					if (quantidadeADeduzir <= 0) break;

					double aRetirarDesteLote = Math.min(quantidadeADeduzir, lote.quantidadeRestante);
					double novaQuantidadeLote = lote.quantidadeRestante - aRetirarDesteLote;

					pstmtAtualizarLote.setDouble(1, novaQuantidadeLote);
					pstmtAtualizarLote.setInt(2, lote.id);
					pstmtAtualizarLote.addBatch();
					
					quantidadeADeduzir -= aRetirarDesteLote;
				}
			}
			
			pstmtAtualizarTotal.executeBatch();
			pstmtAtualizarLote.executeBatch();
		}
	}

	/**
	 * Registra uma venda, incluindo os produtos vendidos, e atualiza o estoque de insumos e o saldo do empreendimento de forma transacional.
	 * @param venda         O objeto Venda contendo os dados da transação.
	 * @param vendaProdutos A lista de produtos que fazem parte da venda.
	 * @throws SQLException Lançada se ocorrer um erro no banco de dados durante a transação.
	 */
	public void registrarVenda(Venda venda, List<VendaProduto> vendaProdutos) throws SQLException {
		String sqlVendas = "INSERT INTO venda (data_venda, valor_total, empreendimento_id) VALUES (?, ?, ?)";
		String sqlVendaProduto = "INSERT INTO venda_produto (preco_unitario, quantidade, produto_id, venda_id) VALUES (?, ?, ?, ?)";
		String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";
		
		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);

			int vendaId;
			try (PreparedStatement pstmtVenda = con.prepareStatement(sqlVendas, Statement.RETURN_GENERATED_KEYS)) {
				pstmtVenda.setDate(1, Date.valueOf(venda.getDataVenda()));
				pstmtVenda.setDouble(2, venda.getValorTotal());
				pstmtVenda.setInt(3, venda.getEmpreendimentoId());
				pstmtVenda.executeUpdate();
				try (ResultSet rs = pstmtVenda.getGeneratedKeys()) {
					if (rs.next()) {
						vendaId = rs.getInt(1);
					} else {
						throw new SQLException("Falha ao obter ID da venda.");
					}
				}
			}

			try (PreparedStatement pstmtVendaProduto = con.prepareStatement(sqlVendaProduto)) {
				for (VendaProduto produto : vendaProdutos) {
					pstmtVendaProduto.setDouble(1, produto.getPrecoUnitario());
					pstmtVendaProduto.setDouble(2, produto.getQuantidade());
					pstmtVendaProduto.setInt(3, produto.getProdutoId());
					pstmtVendaProduto.setInt(4, vendaId);
					pstmtVendaProduto.addBatch();
				}
				pstmtVendaProduto.executeBatch();
			}

			Map<Integer, Double> produtosVendidosMap = vendaProdutos.stream()
				.collect(Collectors.toMap(VendaProduto::getProdutoId, VendaProduto::getQuantidade));
			Map<Integer, Double> insumosParaDeduzir = obterInsumosParaDeduzirDoEstoque(con, produtosVendidosMap);
			
			if (!insumosParaDeduzir.isEmpty()) {
				deduzirInsumosDosLotesFIFO(con, insumosParaDeduzir);
			}

			try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
				pstmtSaldo.setDouble(1, venda.getValorTotal());
				pstmtSaldo.setInt(2, venda.getEmpreendimentoId());
				pstmtSaldo.executeUpdate();
			}

			con.commit();
			
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao registrar a venda. Realizando rollback.", e);
			if (con != null) con.rollback();
			throw e;
		} finally {
			if (con != null) con.close();
		}
	}
	
	/**
	 * Edita uma venda existente, atualizando seus dados, recalculando o valor total e ajustando o estoque de insumos e o saldo do empreendimento.
	 * @param venda         O objeto Venda contendo os dados novos e o ID da venda a ser editada.
	 * @param novosProdutos A nova lista de produtos que irão compor a venda.
	 * @throws SQLException Lançada se ocorrer um erro no banco de dados durante a transação.
	 */
	public void editarVenda(Venda venda, List<VendaProduto> novosProdutos) throws SQLException {
		// Este método precisaria de uma lógica de estorno/reaplicação de estoque similar à dedução para ser 100% preciso.
		// A implementação atual ajusta o estoque total, mas não os lotes individuais.
		
		String sqlBuscarVendaAntiga = "SELECT valor_total, empreendimento_id FROM venda WHERE id = ?";
		String sqlBuscarProdutosAntigos = "SELECT produto_id, quantidade FROM venda_produto WHERE venda_id = ?";
		String sqlUpdateVenda = "UPDATE venda SET data_venda = ?, valor_total = ? WHERE id = ?";
		String sqlDeleteProdutosAntigos = "DELETE FROM venda_produto WHERE venda_id = ?";
		String sqlInsertNovosProdutos = "INSERT INTO venda_produto (preco_unitario, quantidade, produto_id, venda_id) VALUES (?, ?, ?, ?)";
		String sqlAjustarEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?";
		String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";

		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);

			double valorAntigo = 0;
			int empreendimentoId = venda.getEmpreendimentoId();
			Map<Integer, Double> produtosAntigosMap = new HashMap<>();

			try (PreparedStatement pstmtBuscaVenda = con.prepareStatement(sqlBuscarVendaAntiga)) {
				pstmtBuscaVenda.setInt(1, venda.getId());
				try (ResultSet rs = pstmtBuscaVenda.executeQuery()) {
					if (rs.next()) {
						valorAntigo = rs.getDouble("valor_total");
					} else {
						throw new SQLException("Erro na edição: Venda com ID " + venda.getId() + " não encontrada.");
					}
				}
			}
			try (PreparedStatement pstmtBuscaProdutos = con.prepareStatement(sqlBuscarProdutosAntigos)) {
				pstmtBuscaProdutos.setInt(1, venda.getId());
				try (ResultSet rs = pstmtBuscaProdutos.executeQuery()) {
					while (rs.next()) {
						produtosAntigosMap.put(rs.getInt("produto_id"), rs.getDouble("quantidade"));
					}
				}
			}

			Map<Integer, Double> insumosAntigos = obterInsumosParaDeduzirDoEstoque(con, produtosAntigosMap);
			Map<Integer, Double> produtosNovosMap = novosProdutos.stream()
				.collect(Collectors.toMap(VendaProduto::getProdutoId, VendaProduto::getQuantidade));
			Map<Integer, Double> insumosNovos = obterInsumosParaDeduzirDoEstoque(con, produtosNovosMap);
			Map<Integer, Double> diferencaInsumos = new HashMap<>(insumosAntigos);

			insumosNovos.forEach((insumoId, qtd) -> diferencaInsumos.merge(insumoId, qtd, (oldValue, newValue) -> oldValue - newValue));

			try (PreparedStatement pstmtAjustarEstoque = con.prepareStatement(sqlAjustarEstoqueInsumo)) {
				for (Map.Entry<Integer, Double> entry : diferencaInsumos.entrySet()) {
					pstmtAjustarEstoque.setDouble(1, entry.getValue());
					pstmtAjustarEstoque.setInt(2, entry.getKey());
					pstmtAjustarEstoque.addBatch();
				}
				pstmtAjustarEstoque.executeBatch();
			}

			try (PreparedStatement pstmtDeleteProdutos = con.prepareStatement(sqlDeleteProdutosAntigos)) {
				pstmtDeleteProdutos.setInt(1, venda.getId());
				pstmtDeleteProdutos.executeUpdate();
			}
			try (PreparedStatement pstmtInsertProdutos = con.prepareStatement(sqlInsertNovosProdutos)) {
				for (VendaProduto produto : novosProdutos) {
					pstmtInsertProdutos.setDouble(1, produto.getPrecoUnitario());
					pstmtInsertProdutos.setDouble(2, produto.getQuantidade());
					pstmtInsertProdutos.setInt(3, produto.getProdutoId());
					pstmtInsertProdutos.setInt(4, venda.getId());
					pstmtInsertProdutos.addBatch();
				}
				pstmtInsertProdutos.executeBatch();
			}

			try (PreparedStatement pstmtUpdateVenda = con.prepareStatement(sqlUpdateVenda)) {
				pstmtUpdateVenda.setDate(1, Date.valueOf(venda.getDataVenda()));
				pstmtUpdateVenda.setDouble(2, venda.getValorTotal());
				pstmtUpdateVenda.setInt(3, venda.getId());
				pstmtUpdateVenda.executeUpdate();
			}
			
			double diferencaValor = venda.getValorTotal() - valorAntigo;
			try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
				pstmtSaldo.setDouble(1, diferencaValor);
				pstmtSaldo.setInt(2, empreendimentoId);
				pstmtSaldo.executeUpdate();
			}

			con.commit();

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao EDITAR a venda. Venda ID: " + venda.getId(), e);
			if (con != null) {
				try {
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
	 * Realiza a exclusão lógica de uma venda, revertendo o saldo do empreendimento e o estoque de insumos.
	 * @param vendaId          ID da venda a ser marcada como excluída.
	 * @param empreendimentoId ID do empreendimento para verificação de propriedade.
	 * @return                 `true` se a exclusão foi bem-sucedida, `false` caso contrário.
	 */
	public boolean excluirVenda(int vendaId, int empreendimentoId) {
		String sqlBuscarVenda = "SELECT valor_total FROM venda WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";
		String sqlBuscarProdutosDaVenda = "SELECT produto_id, quantidade FROM venda_produto WHERE venda_id = ?";
		String sqlReverterEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?";
		String sqlReverterSaldo = "UPDATE empreendimento SET saldo = saldo - ? WHERE id = ?";
		String sqlExcluirVenda = "UPDATE venda SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";

		Connection con = null;
		try {
			con = ConnectionFactory.conectar();
			con.setAutoCommit(false);

			double valorTotalVenda = 0;
			Map<Integer, Double> produtosParaReverterMap = new HashMap<>();

			try (PreparedStatement pstmtBuscaVenda = con.prepareStatement(sqlBuscarVenda)) {
				pstmtBuscaVenda.setInt(1, vendaId);
				pstmtBuscaVenda.setInt(2, empreendimentoId);
				try (ResultSet rs = pstmtBuscaVenda.executeQuery()) {
					if (rs.next()) {
						valorTotalVenda = rs.getDouble("valor_total");
					} else {
						throw new SQLException("Venda com ID " + vendaId + " não encontrada ou já excluída.");
					}
				}
			}

			try (PreparedStatement pstmtBuscaProdutos = con.prepareStatement(sqlBuscarProdutosDaVenda)) {
				pstmtBuscaProdutos.setInt(1, vendaId);
				try (ResultSet rs = pstmtBuscaProdutos.executeQuery()) {
					while (rs.next()) {
						produtosParaReverterMap.put(rs.getInt("produto_id"), rs.getDouble("quantidade"));
					}
				}
			}
			
			if (!produtosParaReverterMap.isEmpty()) {
				Map<Integer, Double> insumosParaReverter = obterInsumosParaDeduzirDoEstoque(con, produtosParaReverterMap);
				try (PreparedStatement pstmtReverteEstoque = con.prepareStatement(sqlReverterEstoqueInsumo)) {
					for (Map.Entry<Integer, Double> entry : insumosParaReverter.entrySet()) {
						pstmtReverteEstoque.setDouble(1, entry.getValue());
						pstmtReverteEstoque.setInt(2, entry.getKey());
						pstmtReverteEstoque.addBatch();
					}
					pstmtReverteEstoque.executeBatch();
				}
			}

			try (PreparedStatement pstmtReverteSaldo = con.prepareStatement(sqlReverterSaldo)) {
				pstmtReverteSaldo.setDouble(1, valorTotalVenda);
				pstmtReverteSaldo.setInt(2, empreendimentoId);
				pstmtReverteSaldo.executeUpdate();
			}

			try (PreparedStatement pstmtExcluir = con.prepareStatement(sqlExcluirVenda)) {
				pstmtExcluir.setInt(1, vendaId);
				pstmtExcluir.setInt(2, empreendimentoId);
				int rowsAffected = pstmtExcluir.executeUpdate();
				if (rowsAffected == 0) {
					throw new SQLException("A exclusão lógica da venda falhou, nenhuma linha afetada.");
				}
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao excluir venda com ID: " + vendaId + ". Realizando rollback.", e);
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
	 * Recupera uma lista de todas as vendas ativas de um determinado empreendimento.
	 * @param empreendimentoId O ID do empreendimento.
	 * @return                 Uma `ArrayList` de objetos `Venda`.
	 */
	public ArrayList<Venda> listarVendas(int empreendimentoId) {
		ArrayList<Venda> vendas = new ArrayList<>();
		String sqlListarVendas = "SELECT * FROM venda WHERE empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
			 PreparedStatement pstmt = con.prepareStatement(sqlListarVendas)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Venda venda = new Venda();
				venda.setId(rs.getInt("id"));
				venda.setDataVenda(rs.getString("data_venda"));
				venda.setCreatedAt(rs.getString("created_at"));
				venda.setValorTotal(rs.getDouble("valor_total"));
				vendas.add(venda);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar vendas.", e);
		}
		return vendas;
	}

	/**
	 * Lista todos os produtos associados a uma venda específica.
	 * @param vendaId O ID da venda.
	 * @param con     A conexão ativa com o banco de dados.
	 * @return        Uma lista de objetos `VendaProduto`.
	 * @throws SQLException Lançada se ocorrer um erro na consulta ao banco de dados.
	 */
	private List<VendaProduto> listarProdutosPorVendaId(int vendaId, Connection con) throws SQLException {
		List<VendaProduto> produtos = new ArrayList<>();
		String sqlListarProdutosPorVendaId = "SELECT ci.*, i.nome AS produto_nome " +
											 "FROM venda_produto ci " +
											 "JOIN produto i ON ci.produto_id = i.id " +
											 "WHERE ci.venda_id = ?";
		try (PreparedStatement pstmt = con.prepareStatement(sqlListarProdutosPorVendaId)) {
			pstmt.setInt(1, vendaId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					VendaProduto produto = new VendaProduto();
					produto.setId(rs.getInt("id"));
					produto.setPrecoUnitario(rs.getDouble("preco_unitario"));
					produto.setQuantidade(rs.getDouble("quantidade"));
					produto.setProdutoId(rs.getInt("produto_id"));
					produto.setVendaId(rs.getInt("venda_id"));
					produto.setProdutoNome(rs.getString("produto_nome"));
					produtos.add(produto);
				}
			}
		}
		return produtos;
	}

	/**
	 * Recupera uma venda específica pelo seu ID, incluindo sua lista de produtos associados.
	 * @param id               O ID da venda a ser recuperada.
	 * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
	 * @return                 Um objeto `Venda` preenchido com todos os seus dados e listas, ou `null` se não for encontrada.
	 */
	public Venda obterVendaPorId(int id, int empreendimentoId) {
		Venda venda = null;
		String sqlObterVenda = "SELECT * FROM venda WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";
		try (Connection con = ConnectionFactory.conectar();
			 PreparedStatement pstmt = con.prepareStatement(sqlObterVenda)) {

			pstmt.setInt(1, id);
			pstmt.setInt(2, empreendimentoId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					venda = new Venda();
					venda.setId(rs.getInt("id"));
					venda.setDataVenda(rs.getString("data_venda"));
					venda.setValorTotal(rs.getDouble("valor_total"));
					venda.setEmpreendimentoId(rs.getInt("empreendimento_id"));
					List<VendaProduto> produtosDaVenda = listarProdutosPorVendaId(venda.getId(), con);
					venda.setProdutos(produtosDaVenda);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao obter venda com ID: " + id, e);
		}
		return venda;
	}
}