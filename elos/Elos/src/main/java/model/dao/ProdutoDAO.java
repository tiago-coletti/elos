package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Insumo;
import model.entity.Produto;
import model.entity.ProdutoInsumo;
import model.entity.ProdutoMaoObra;
import util.ConnectionFactory;

public class ProdutoDAO {
	private static final Logger logger = Logger.getLogger(CompraDAO.class.getName());
	
	/**
	 * Inclui um novo produto e todos os seus insumos e mãos de obra associados
	 * de forma transacional, utilizando o padrão try-with-resources.
	 *
	 * @param novoProduto      O objeto Produto a ser inserido.
	 * @param produtoInsumos   A lista de insumos do produto.
	 * @param produtoMaosObra  A lista de mãos de obra do produto.
	 */
	public void incluirProduto(Produto novoProduto, List<ProdutoInsumo> produtoInsumos, List<ProdutoMaoObra> produtoMaosObra) throws SQLException {
	    String sqlProduto = "INSERT INTO produto (nome, preco_venda, empreendimento_id) VALUES (?, ?, ?)";
	    String sqlInsumo = "INSERT INTO produto_insumo (produto_id, insumo_id, quantidade_utilizada) VALUES (?, ?, ?)";
	    String sqlMaoObra = "INSERT INTO produto_mao_obra (produto_id, mao_obra_id, horas_utilizadas) VALUES (?, ?, ?)";
	    
	    try (Connection con = ConnectionFactory.conectar()) {
	        try {
	            con.setAutoCommit(false);
	            
	            int produtoId = 0;

	            // --- Passo 1: Inserir o Produto e obter o ID gerado ---
	            try (PreparedStatement pstmtProduto = con.prepareStatement(sqlProduto, PreparedStatement.RETURN_GENERATED_KEYS)) {
	                pstmtProduto.setString(1, novoProduto.getNome());
	                pstmtProduto.setDouble(2, novoProduto.getPrecoVenda());
	                pstmtProduto.setInt(3, novoProduto.getEmpreendimentoId());
	                
	                int affectedRows = pstmtProduto.executeUpdate();
	                if (affectedRows == 0) {
	                    throw new SQLException("A inserção do produto falhou, nenhuma linha foi afetada.");
	                }

	                try (ResultSet generatedKeys = pstmtProduto.getGeneratedKeys()) {
	                    if (generatedKeys.next()) {
	                        produtoId = generatedKeys.getInt(1);
	                    } else {
	                        throw new SQLException("A inserção do produto falhou, nenhum ID foi obtido.");
	                    }
	                }
	            } 

	            // --- Passo 2: Inserir os Insumos do Produto ---
	            if (produtoInsumos != null && !produtoInsumos.isEmpty()) {
	                try (PreparedStatement pstmtInsumo = con.prepareStatement(sqlInsumo)) {
	                    for (ProdutoInsumo item : produtoInsumos) {
	                        pstmtInsumo.setInt(1, produtoId);
	                        pstmtInsumo.setInt(2, item.getInsumoId());
	                        pstmtInsumo.setDouble(3, item.getQuantidadeUtilizada());
	                        pstmtInsumo.addBatch();
	                    }
	                    pstmtInsumo.executeBatch();
	                } 
	            }

	            // --- Passo 3: Inserir as Mãos de Obra do Produto ---
	            if (produtoMaosObra != null && !produtoMaosObra.isEmpty()) {
	                try (PreparedStatement pstmtMaoObra = con.prepareStatement(sqlMaoObra)) {
	                    for (ProdutoMaoObra item : produtoMaosObra) {
	                        pstmtMaoObra.setInt(1, produtoId);
	                        pstmtMaoObra.setInt(2, item.getMaoObraId());
	                        pstmtMaoObra.setDouble(3, item.getHorasUtilizadas());
	                        pstmtMaoObra.addBatch();
	                    }
	                    pstmtMaoObra.executeBatch();
	                } 
	            }

	            con.commit();
	        } catch (SQLException e) {
	            logger.log(Level.WARNING, "Erro durante a transação. Revertendo alterações (rollback)...");
	            con.rollback();
	            throw e; 
	        }
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
	 * Recupera todos os produtos da tabela "produto" de determinado empreendimento.
	 * 
	 * @return Lista de produtos cadastradas no banco de dados.
	 */
	public ArrayList<Produto> listarProdutos(int empreendimentoId) {
		ArrayList<Produto> produtos = new ArrayList<>();
		String sqlListarProdutos = "SELECT * FROM produto WHERE empreendimento_id = ? AND deleted_at IS NULL";

		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pstmt = con.prepareStatement(sqlListarProdutos)) {

			pstmt.setInt(1, empreendimentoId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Produto produto = new Produto();
				produto.setId(rs.getInt("id"));
				produto.setNome(rs.getString("nome"));
				produto.setPrecoVenda(rs.getDouble("preco_venda"));
				produto.setCreatedAt(rs.getString("created_at"));

				produtos.add(produto);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar produtos.", e);
		}
		return produtos;
	}
	
	/**
	 * Método auxiliar para listar todos os insumos de um produto específico.
	 *
	 * @param produtoId O ID da produto da qual queremos os insumos.
	 * @param con A conexão de banco de dados existente para evitar criar uma nova.
	 * @return Uma lista de objetos ProdutoInsumo.
	 * @throws SQLException
	 */
	private List<ProdutoInsumo> listarInsumosPorProdutoId(int produtoId, Connection con) throws SQLException {
	    List<ProdutoInsumo> insumos = new ArrayList<>();
	    
	    String sqlListarInsumosPorProdutoId = "SELECT ci.*, i.nome AS insumo_nome " +
	                 "FROM produto_insumo ci " +
	                 "JOIN insumo i ON ci.insumo_id = i.id " +
	                 "WHERE ci.produto_id = ?";

	    try (PreparedStatement pstmt = con.prepareStatement(sqlListarInsumosPorProdutoId)) {
	        pstmt.setInt(1, produtoId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProdutoInsumo insumo = new ProdutoInsumo();
	                insumo.setId(rs.getInt("id"));
	                insumo.setQuantidadeUtilizada(rs.getDouble("quantidade_utilizada"));
	                insumo.setInsumoId(rs.getInt("insumo_id"));
	                insumo.setProdutoId(rs.getInt("produto_id"));
	                
	                insumo.setInsumoNome(rs.getString("insumo_nome")); 
	                insumos.add(insumo);
	            }
	        }
	    }
	    return insumos;
	}
	
	/**
	 * Método auxiliar para listar todas as mãos de obra de um produto específico.
	 *
	 * @param produtoId O ID da produto da qual queremos os insumos.
	 * @param con A conexão de banco de dados existente para evitar criar uma nova.
	 * @return Uma lista de objetos ProdutoMaoObra.
	 * @throws SQLException
	 */
	private List<ProdutoMaoObra> listarMaoObraPorProdutoId(int produtoId, Connection con) throws SQLException {
	    List<ProdutoMaoObra> maosObra = new ArrayList<>();
	    
	    String sqlListarMaoObraPorProdutoId = "SELECT ci.*, i.nome AS mao_obra_nome " +
	                 "FROM produto_mao_obra ci " +
	                 "JOIN mao_obra i ON ci.mao_obra_id = i.id " +
	                 "WHERE ci.produto_id = ?";

	    try (PreparedStatement pstmt = con.prepareStatement(sqlListarMaoObraPorProdutoId)) {
	        pstmt.setInt(1, produtoId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ProdutoMaoObra maoObra = new ProdutoMaoObra();
	                maoObra.setId(rs.getInt("id"));
	                maoObra.setHorasUtilizadas(rs.getDouble("horas_utilizadas"));
	                maoObra.setMaoObraId(rs.getInt("mao_obra_id"));
	                maoObra.setProdutoId(rs.getInt("produto_id"));
	                
	                maoObra.setMaoObraNome(rs.getString("mao_obra_nome")); 
	                maosObra.add(maoObra);
	            }
	        }
	    }
	    return maosObra;
	}
	
	/**
	 * Recupera um produto pelo seu ID, incluindo todos os seus insumos associados.
	 *
	 * @param id O ID do produto a ser recuperado.
	 * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
	 * @return Objeto Produto com os dados, a lista de insumos e de mãos de obras preenchidas, ou null se não for encontrado.
	 */
	public Produto obterProdutoPorId(int id, int empreendimentoId) {
	    Produto produto = null;
	    String sqlObterProdutoPorId = "SELECT * FROM produto WHERE id = ? AND empreendimento_id = ? AND deleted_at IS NULL";

	    try (Connection con = ConnectionFactory.conectar();
	         PreparedStatement pstmt = con.prepareStatement(sqlObterProdutoPorId)) {

	        pstmt.setInt(1, id);
	        pstmt.setInt(2, empreendimentoId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                produto.setId(rs.getInt("id"));
	                produto.setNome(rs.getString("nome"));
	                produto.setPrecoVenda(rs.getDouble("preco_venda"));
	                produto.setCreatedAt(rs.getString("created_at"));

	                List<ProdutoInsumo> insumosDoProduto = listarInsumosPorProdutoId(produto.getId(), con);
	                List<ProdutoMaoObra> maosObraDoProduto = listarMaoObraPorProdutoId(produto.getId(), con);
	                
	                produto.setInsumos(insumosDoProduto);
	                produto.setMaosObra(maosObraDoProduto);
	            }
	        }
	    } catch (SQLException e) {
	        logger.log(Level.SEVERE, "Erro ao obter compra com ID: " + id, e);
	    }

	    return produto;
	}
}