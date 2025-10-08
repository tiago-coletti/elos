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
	
	/**
	 * Obtém um mapa consolidado de todos os insumos e suas respectivas quantidades totais que precisam ser deduzidas do estoque com base em uma lista de produtos vendidos.
	 * @param con 				A conexão ativa com o banco de dados.
	 * @param produtosVendidos	Um mapa onde a chave é o ID do produto e o valor é a quantidade vendida.
	 * @return         			Um mapa onde a chave é o ID do insumo e o valor é a quantidade total a ser deduzida do estoque.
	 */
	private Map<Integer, Double> obterInsumosParaDeduzirDoEstoque(Connection con, Map<Integer, Double> produtosVendidos) throws SQLException {
	    String sqlBuscarInsumos = "SELECT insumo_id, quantidade_utilizada FROM produto_insumo WHERE produto_id = ?";
	    
	    Map<Integer, Double> insumosADeduzir = new HashMap<>();
	    if (produtosVendidos == null || produtosVendidos.isEmpty()) {
	        return insumosADeduzir;
	    }
	    
	    try (PreparedStatement pstmt = con.prepareStatement(sqlBuscarInsumos)) {
	        
	        // Itera sobre cada produto da venda
	        for (Map.Entry<Integer, Double> produtoVendido : produtosVendidos.entrySet()) {
	            Integer produtoId = produtoVendido.getKey();
	            Double quantidadeVendida = produtoVendido.getValue();

	            // Define o ID do produto na query
	            pstmt.setInt(1, produtoId);

	            // Executa a busca pelos insumos daquele produto
	            try (ResultSet rs = pstmt.executeQuery()) {
	                
	                // Para cada insumo encontrado...
	                while (rs.next()) {
	                    int insumoId = rs.getInt("insumo_id");
	                    double quantidadePorProduto = rs.getDouble("quantidade_utilizada");

	                    // Calcula o total daquele insumo para a quantidade vendida do produto
	                    double quantidadeTotalInsumo = quantidadePorProduto * quantidadeVendida;

	                    // Adiciona (ou soma) ao mapa final. merge() simplifica a lógica de ver se a chave já existe.
	                    insumosADeduzir.merge(insumoId, quantidadeTotalInsumo, Double::sum);
	                }
	            }
	        }
	    }
	    
	    return insumosADeduzir;
	}

	/**
	 * Registra uma venda, incluindo o pedido, os produtos e atualiza o estoque de insumos e o saldo.
	 * @param venda         Venda que referencia a venda realizada pelo aluno.
	 * @param vendaProdutos Lista de produtos que fazem parte da venda.
	 */
	public void registrarVenda(Venda venda, List<VendaProduto> vendaProdutos) throws SQLException {
	    
	    String sqlVendas = "INSERT INTO venda (data_venda, valor_total, empreendimento_id) VALUES (?, ?, ?)";
	    String sqlVendaProduto = "INSERT INTO venda_produto (preco_unitario, quantidade, produto_id, venda_id) VALUES (?, ?, ?, ?)";
	    String sqlAtualizarEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade - ? WHERE id = ?"; // CORREÇÃO: É uma subtração
	    String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?";
 
	    Connection con = null; 
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false);

	        // 1. Registrar a venda e obter o ID gerado
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
	                    throw new SQLException("Falha ao obter ID da venda, nenhuma chave gerada.");
	                }
	            }
	        }

	        // 2. Registrar produtos da venda
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

	        // 3. Obter e atualizar o estoque de insumos com base nos produtos vendidos
	        Map<Integer, Double> produtosVendidosMap = vendaProdutos.stream()
	            .collect(Collectors.toMap(VendaProduto::getProdutoId, VendaProduto::getQuantidade));
	        
	        Map<Integer, Double> insumosParaDeduzir = obterInsumosParaDeduzirDoEstoque(con, produtosVendidosMap);

	        try (PreparedStatement pstmtAtualizarInsumo = con.prepareStatement(sqlAtualizarEstoqueInsumo)) {
	            for (Map.Entry<Integer, Double> entry : insumosParaDeduzir.entrySet()) {
	                pstmtAtualizarInsumo.setDouble(1, entry.getValue()); 
	                pstmtAtualizarInsumo.setInt(2, entry.getKey());   
	                pstmtAtualizarInsumo.addBatch();
	            }
	            pstmtAtualizarInsumo.executeBatch();
	        }

	        // 4. Atualizar saldo do empreendimento
	        try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
	            pstmtSaldo.setDouble(1, venda.getValorTotal());
	            pstmtSaldo.setInt(2, venda.getEmpreendimentoId());
	            pstmtSaldo.executeUpdate();
	        }

	        con.commit();
	        
	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao registrar a venda com produto. Empreendimento ID: " + venda.getEmpreendimentoId(), e);
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
	 * Edita uma venda existente, atualizando seus dados, recalculando o valor total,
	 * substituindo os produtos antigos pelos novos, ajustando o saldo do empreendimento e o estoque de insumos.
	 *
	 * @param venda       O objeto Venda contendo os DADOS NOVOS e o ID da venda a ser editada.
	 * @param novosProdutos A nova lista de produtos que irão compor a venda.
	 */
	public void editarVenda(Venda venda, List<VendaProduto> novosProdutos) throws SQLException {

	    String sqlBuscarVendaAntiga = "SELECT valor_total, empreendimento_id FROM venda WHERE id = ?";
	    String sqlBuscarProdutosAntigos = "SELECT produto_id, quantidade FROM venda_produto WHERE venda_id = ?";
	    String sqlUpdateVenda = "UPDATE venda SET data_venda = ?, valor_total = ? WHERE id = ?";
	    String sqlDeleteProdutosAntigos = "DELETE FROM venda_produto WHERE venda_id = ?";
	    String sqlInsertNovosProdutos = "INSERT INTO venda_produto (preco_unitario, quantidade, produto_id, venda_id) VALUES (?, ?, ?, ?)";
	    String sqlAjustarEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?"; // Usamos + pois a diferença pode ser positiva ou negativa
	    String sqlAtualizarSaldo = "UPDATE empreendimento SET saldo = saldo + ? WHERE id = ?"; // Apenas a diferença de valor

	    Connection con = null;
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false); 

	        double valorAntigo = 0;
	        int empreendimentoId = venda.getEmpreendimentoId(); // Assume que não muda
	        Map<Integer, Double> produtosAntigosMap = new HashMap<>();

	        // 1. Buscar dados antigos da venda (valor e produtos)
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

	        // 2. Calcular o ajuste de estoque
	        Map<Integer, Double> insumosAntigos = obterInsumosParaDeduzirDoEstoque(con, produtosAntigosMap);
	        Map<Integer, Double> produtosNovosMap = novosProdutos.stream()
	            .collect(Collectors.toMap(VendaProduto::getProdutoId, VendaProduto::getQuantidade));
	        Map<Integer, Double> insumosNovos = obterInsumosParaDeduzirDoEstoque(con, produtosNovosMap);
	        Map<Integer, Double> diferencaInsumos = new HashMap<>(insumosAntigos);

	        // Subtrai as novas quantidades das antigas para achar a diferença
	        insumosNovos.forEach((insumoId, qtd) -> diferencaInsumos.merge(insumoId, qtd, (oldValue, newValue) -> oldValue - newValue));

	        try (PreparedStatement pstmtAjustarEstoque = con.prepareStatement(sqlAjustarEstoqueInsumo)) {
	            for (Map.Entry<Integer, Double> entry : diferencaInsumos.entrySet()) {
	                // Se a diferença for positiva, devolve ao estoque. Se negativa, retira.
	                pstmtAjustarEstoque.setDouble(1, entry.getValue());
	                pstmtAjustarEstoque.setInt(2, entry.getKey());
	                pstmtAjustarEstoque.addBatch();
	            }
	            pstmtAjustarEstoque.executeBatch();
	        }

	        // 3. Deletar os produtos antigos e inserir os novos
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

	        // 4. Atualizar os dados da própria venda (data e novo valor)
	        try (PreparedStatement pstmtUpdateVenda = con.prepareStatement(sqlUpdateVenda)) {
	            pstmtUpdateVenda.setDate(1, Date.valueOf(venda.getDataVenda()));
	            pstmtUpdateVenda.setDouble(2, venda.getValorTotal());
	            pstmtUpdateVenda.setInt(3, venda.getId());
	            pstmtUpdateVenda.executeUpdate();
	        }
	        
	        // 5. Ajustar o saldo do empreendimento com a diferença
	        double diferencaValor = venda.getValorTotal() - valorAntigo;
	        try (PreparedStatement pstmtSaldo = con.prepareStatement(sqlAtualizarSaldo)) {
	            pstmtSaldo.setDouble(1, diferencaValor);
	            pstmtSaldo.setInt(2, empreendimentoId);
	            pstmtSaldo.executeUpdate();
	        }

	        con.commit(); 

	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao EDITAR a venda com produto. Venda ID: " + venda.getId(), e);
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
	 * Realiza a exclusão lógica (soft delete) de uma venda, revertendo o valor do saldo do empreendimento
	 * e devolvendo as quantidades de insumos ao estoque.
	 *
	 * @param vendaId         ID da venda a ser marcada como excluída.
	 * @param empreendimentoId ID do empreendimento para verificação de propriedade.
	 * @return                 true se a exclusão completa foi bem-sucedida, false caso contrário.
	 */
	public boolean excluirVenda(int vendaId, int empreendimentoId) {
	    String sqlBuscarVenda = "SELECT valor_total FROM venda WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";
	    String sqlBuscarProdutosDaVenda = "SELECT produto_id, quantidade FROM venda_produto WHERE venda_id = ?";
	    String sqlReverterEstoqueInsumo = "UPDATE insumo SET quantidade = quantidade + ? WHERE id = ?"; // CORREÇÃO: Devolve ao estoque
	    String sqlReverterSaldo = "UPDATE empreendimento SET saldo = saldo - ? WHERE id = ?"; // CORREÇÃO: Subtrai do saldo
	    String sqlExcluirVenda = "UPDATE venda SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";

	    Connection con = null;
	    try {
	        con = ConnectionFactory.conectar();
	        con.setAutoCommit(false); 

	        double valorTotalVenda = 0;
	        Map<Integer, Double> produtosParaReverterMap = new HashMap<>();

	        // 1. Buscar o valor total da venda a ser excluída
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

	        // 2. Buscar todos os produtos e quantidades da venda
	        try (PreparedStatement pstmtBuscaProdutos = con.prepareStatement(sqlBuscarProdutosDaVenda)) {
	            pstmtBuscaProdutos.setInt(1, vendaId);
	            try (ResultSet rs = pstmtBuscaProdutos.executeQuery()) {
	                while (rs.next()) {
	                    produtosParaReverterMap.put(rs.getInt("produto_id"), rs.getDouble("quantidade"));
	                }
	            }
	        }
	        
	        // 3. Calcular e reverter o estoque (devolver insumos)
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

	        // 4. Reverter o saldo (subtrair o valor da venda do empreendimento)
	        try (PreparedStatement pstmtReverteSaldo = con.prepareStatement(sqlReverterSaldo)) {
	            pstmtReverteSaldo.setDouble(1, valorTotalVenda);
	            pstmtReverteSaldo.setInt(2, empreendimentoId);
	            pstmtReverteSaldo.executeUpdate();
	        }

	        // 5. Marcar a venda como excluída (soft delete)
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
	        logger.log(Level.SEVERE, "Erro ao realizar exclusão da venda com ID: " + vendaId + ". Realizando rollback.", e);
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
	 * Recupera todos as vendas da tabela "venda" de determinado empreendimento.
	 * 
	 * @return Lista de vendas cadastradas no banco de dados.
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
			logger.log(Level.SEVERE, "Erro ao listar produtos.", e);
		}
		return vendas;
	}
	
	/**
	 * Método auxiliar para listar todos os produtos de uma venda específica.
	 *
	 * @param vendaId O ID da venda da qual queremos os produtos.
	 * @param con A conexão de banco de dados existente para evitar criar uma nova.
	 * @return Uma lista de objetos VendaProduto.
	 * @throws SQLException
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
	                produto.setQuantidade(rs.getDouble("quantidade_vendada"));
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
	 * Recupera uma venda pelo seu ID, incluindo todos os seus produtos associados.
	 *
	 * @param id O ID da venda a ser recuperada.
	 * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
	 * @return Objeto Venda com os dados e a lista de produtos preenchida, ou null se não for encontrada.
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