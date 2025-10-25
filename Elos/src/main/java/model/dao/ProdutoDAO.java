package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Insumo;
import model.entity.MaoObra;
import model.entity.Produto;
import model.entity.ProdutoInsumo;
import model.entity.ProdutoMaoObra;
import util.ConnectionFactory;

public class ProdutoDAO {
    private static final Logger logger = Logger.getLogger(ProdutoDAO.class.getName());

    /**
     * Inclui um novo produto e suas associações com insumos e mãos de obra de forma transacional.
     * @param novoProduto     O objeto Produto a ser inserido.
     * @param produtoInsumos  A lista de insumos associados ao produto.
     * @param produtoMaosObra A lista de mãos de obra associadas ao produto.
     * @throws SQLException   Lançada se ocorrer um erro no banco de dados durante a transação.
     */
    public void incluirProduto(Produto novoProduto, List<ProdutoInsumo> produtoInsumos, List<ProdutoMaoObra> produtoMaosObra) throws SQLException {
        String sqlProduto = "INSERT INTO produto (nome, preco_venda, empreendimento_id) VALUES (?, ?, ?)";
        String sqlInsumo = "INSERT INTO produto_insumo (produto_id, insumo_id, quantidade_utilizada) VALUES (?, ?, ?)";
        String sqlMaoObra = "INSERT INTO produto_mao_obra (produto_id, mao_obra_id, horas_utilizadas) VALUES (?, ?, ?)";
        
        try (Connection con = ConnectionFactory.conectar()) {
            try {
                con.setAutoCommit(false);
                
                int produtoId = 0;

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
     * Atualiza um produto existente e suas associações, substituindo os insumos e mãos de obra antigos pelos novos, de forma transacional.
     * @param produtoAtualizado O objeto Produto com os dados atualizados (incluindo o ID).
     * @param novosInsumos      A nova lista completa de insumos do produto.
     * @param novasMaosObra     A nova lista completa de mãos de obra do produto.
     * @throws SQLException      Lançada se ocorrer um erro no banco de dados durante a transação.
     */
    public void editarProduto(Produto produtoAtualizado, List<ProdutoInsumo> novosInsumos, List<ProdutoMaoObra> novasMaosObra) throws SQLException {
        String sqlUpdateProduto = "UPDATE produto SET nome = ?, preco_venda = ? WHERE id = ?";
        String sqlDeletarInsumos = "DELETE FROM produto_insumo WHERE produto_id = ?";
        String sqlDeletarMaosObra = "DELETE FROM produto_mao_obra WHERE produto_id = ?";
        String sqlInserirInsumo = "INSERT INTO produto_insumo (produto_id, insumo_id, quantidade_utilizada) VALUES (?, ?, ?)";
        String sqlInserirMaoObra = "INSERT INTO produto_mao_obra (produto_id, mao_obra_id, horas_utilizadas) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.conectar()) {
            try {
                con.setAutoCommit(false);
                
                int produtoId = produtoAtualizado.getId();

                try (PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdateProduto)) {
                    pstmtUpdate.setString(1, produtoAtualizado.getNome());
                    pstmtUpdate.setDouble(2, produtoAtualizado.getPrecoVenda());
                    pstmtUpdate.setInt(3, produtoId);
                    pstmtUpdate.executeUpdate();
                }

                try (PreparedStatement pstmtDeleteInsumos = con.prepareStatement(sqlDeletarInsumos)) {
                    pstmtDeleteInsumos.setInt(1, produtoId);
                    pstmtDeleteInsumos.executeUpdate();
                }
                try (PreparedStatement pstmtDeleteMaosObra = con.prepareStatement(sqlDeletarMaosObra)) {
                    pstmtDeleteMaosObra.setInt(1, produtoId);
                    pstmtDeleteMaosObra.executeUpdate();
                }

                if (novosInsumos != null && !novosInsumos.isEmpty()) {
                    try (PreparedStatement pstmtInsumo = con.prepareStatement(sqlInserirInsumo)) {
                        for (ProdutoInsumo item : novosInsumos) {
                            pstmtInsumo.setInt(1, produtoId);
                            pstmtInsumo.setInt(2, item.getInsumoId());
                            pstmtInsumo.setDouble(3, item.getQuantidadeUtilizada());
                            pstmtInsumo.addBatch();
                        }
                        pstmtInsumo.executeBatch();
                    }
                }

                if (novasMaosObra != null && !novasMaosObra.isEmpty()) {
                    try (PreparedStatement pstmtMaoObra = con.prepareStatement(sqlInserirMaoObra)) {
                        for (ProdutoMaoObra item : novasMaosObra) {
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
                logger.log(Level.WARNING, "Erro durante a transação de atualização do produto. Revertendo alterações (rollback)...", e);
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Realiza a exclusão lógica (soft delete) de um produto, definindo a data de exclusão.
     * @param id               O ID do produto a ser excluído.
     * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
     * @return                 `true` se a exclusão foi bem-sucedida, `false` caso contrário.
     */
    public boolean excluirProduto(int id, int empreendimentoId) {
        String sqlExcluirProduto = "UPDATE produto SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND empreendimento_id = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pstmt = con.prepareStatement(sqlExcluirProduto)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, empreendimentoId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao excluir produto com ID: " + id, e);
        }
        return false;
    }
    
    /**
     * Recupera uma lista de todos os produtos ativos de um determinado empreendimento.
     * @param empreendimentoId O ID do empreendimento do qual os produtos serão listados.
     * @return                 Uma `ArrayList` de objetos `Produto`.
     */
    public ArrayList<Produto> listarProdutos(int empreendimentoId) {
        ArrayList<Produto> produtos = new ArrayList<>();
        String sqlListarProdutos = "SELECT * FROM produto WHERE empreendimento_id = ? AND deleted_at IS NULL ORDER BY id DESC";

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
     * CORRIGIDO: Lista todos os insumos associados a um produto, calculando o custo de cada insumo com base no lote de compra mais antigo com estoque disponível (FIFO).
     * @param produtoId O ID do produto para o qual os insumos serão listados.
     * @param con       A conexão de banco de dados ativa para ser usada na transação.
     * @return          Uma `List` de objetos `ProdutoInsumo`, com os custos calculados.
     * @throws SQLException Lançada se ocorrer um erro na consulta ao banco de dados.
     */
    private List<ProdutoInsumo> listarInsumosPorProdutoId(int produtoId, Connection con) throws SQLException {
        List<ProdutoInsumo> insumos = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  pi.id, pi.quantidade_utilizada, pi.insumo_id, pi.produto_id, " +
                     "  i.nome, i.unidade_medida, " +
                     "  (SELECT ci.preco_unitario " +
                     "   FROM compra_insumo ci " +
                     "   JOIN compra c ON ci.compra_id = c.id " +
                     "   WHERE ci.insumo_id = i.id " +
                     "     AND ci.quantidade_restante > 0 " + 
                     "   ORDER BY c.data_compra ASC, c.id ASC " + 
                     "   LIMIT 1) AS custo_fifo " + 
                     "FROM produto_insumo pi " +
                     "JOIN insumo i ON pi.insumo_id = i.id " +
                     "WHERE pi.produto_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Insumo insumoObj = new Insumo();
                    insumoObj.setId(rs.getInt("insumo_id"));
                    insumoObj.setNome(rs.getString("nome"));
                    insumoObj.setUnidadeMedida(rs.getString("unidade_medida"));
                    insumoObj.setCustoEstimado(rs.getDouble("custo_fifo")); // Usando o custo FIFO calculado

                    ProdutoInsumo produtoInsumo = new ProdutoInsumo();
                    produtoInsumo.setId(rs.getInt("id"));
                    produtoInsumo.setQuantidadeUtilizada(rs.getDouble("quantidade_utilizada"));
                    produtoInsumo.setInsumoId(rs.getInt("insumo_id"));
                    produtoInsumo.setProdutoId(rs.getInt("produto_id"));
                    produtoInsumo.setInsumo(insumoObj);

                    insumos.add(produtoInsumo);
                }
            }
        }
        return insumos;
    }

    /**
     * Lista todas as mãos de obra associadas a um produto específico, incluindo os detalhes de cada etapa.
     * @param produtoId O ID do produto para o qual as mãos de obra serão listadas.
     * @param con       A conexão de banco de dados ativa para ser usada na transação.
     * @return          Uma `List` de objetos `ProdutoMaoObra`.
     * @throws SQLException Lançada se ocorrer um erro na consulta ao banco de dados.
     */
    private List<ProdutoMaoObra> listarMaoObraPorProdutoId(int produtoId, Connection con) throws SQLException {
        List<ProdutoMaoObra> maosObra = new ArrayList<>();
        String sql = "SELECT pmo.id, pmo.horas_utilizadas, pmo.mao_obra_id, pmo.produto_id, " +
                     "m.nome, m.custo_hora " + 
                     "FROM produto_mao_obra pmo " +
                     "JOIN mao_obra m ON pmo.mao_obra_id = m.id " +
                     "WHERE pmo.produto_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, produtoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MaoObra maoObraObj = new MaoObra();
                    maoObraObj.setId(rs.getInt("mao_obra_id"));
                    maoObraObj.setNome(rs.getString("nome"));
                    maoObraObj.setCustoHora(rs.getDouble("custo_hora"));

                    ProdutoMaoObra produtoMaoObra = new ProdutoMaoObra();
                    produtoMaoObra.setId(rs.getInt("id"));
                    produtoMaoObra.setHorasUtilizadas(rs.getDouble("horas_utilizadas"));
                    produtoMaoObra.setMaoObraId(rs.getInt("mao_obra_id"));
                    produtoMaoObra.setProdutoId(rs.getInt("produto_id"));
                    produtoMaoObra.setMaoObra(maoObraObj);
                    
                    maosObra.add(produtoMaoObra);
                }
            }
        }
        return maosObra;
    }
    
    /**
     * Recupera um produto específico pelo seu ID, incluindo suas listas de insumos e mãos de obra associadas.
     * @param id               O ID do produto a ser recuperado.
     * @param empreendimentoId O ID do empreendimento para verificação de propriedade.
     * @return                 Um objeto `Produto` preenchido com todos os seus dados e listas, ou `null` se não for encontrado.
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
                    produto = new Produto();
                    
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
            logger.log(Level.SEVERE, "Erro ao obter produto com ID: " + id, e);
        }

        return produto;
    }

    // --- MÉTODOS PARA O DASHBOARD ---

    /**
     * Calcula a receita total obtida com a venda de produtos em um determinado período.
     * @param empreendimentoId O ID do empreendimento.
     * @param periodo          O período a ser considerado (atualmente fixo para "mes").
     * @return                 O valor total da receita.
     */
    public double calcularReceitaTotalPorPeriodo(int empreendimentoId, String periodo) {
        double total = 0.0;
        String sql = "SELECT SUM(vp.preco_unitario * vp.quantidade) AS total " +
                     "FROM venda_produto vp " +
                     "JOIN venda v ON vp.venda_id = v.id " +
                     "WHERE v.empreendimento_id = ? AND v.deleted_at IS NULL " +
                     "AND v.data_venda >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";

        try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, empreendimentoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao calcular receita total de produtos.", e);
        }
        return total;
    }

    /**
     * CORRIGIDO: Calcula o custo total de produção dos produtos vendidos em um determinado período.
     * @param empreendimentoId O ID do empreendimento.
     * @param periodo          O período a ser considerado (atualmente fixo para "mes").
     * @return                 O valor total do custo de produção.
     */
    public double calcularCustoProducaoTotalPorPeriodo(int empreendimentoId, String periodo) {
        double total = 0.0;
        String sql = "WITH CustoProduto AS (" +
                     "    SELECT " +
                     "        p.id AS produto_id, " +
                     "        (" +
                     "            COALESCE((SELECT SUM(pi.quantidade_utilizada * " +
                     "                (SELECT ci.preco_unitario FROM compra_insumo ci JOIN compra c ON ci.compra_id = c.id WHERE ci.insumo_id = pi.insumo_id AND ci.quantidade_restante > 0 ORDER BY c.data_compra ASC, c.id ASC LIMIT 1)" +
                     "            ) FROM produto_insumo pi WHERE pi.produto_id = p.id), 0) + " +
                     "            COALESCE((SELECT SUM(pmo.horas_utilizadas * mo.custo_hora) FROM produto_mao_obra pmo JOIN mao_obra mo ON pmo.mao_obra_id = mo.id WHERE pmo.produto_id = p.id), 0)" +
                     "        ) AS custo_unitario " +
                     "    FROM produto p " +
                     "    WHERE p.empreendimento_id = ? AND p.deleted_at IS NULL" +
                     ") " +
                     "SELECT SUM(vp.quantidade * cp.custo_unitario) AS total_custo " +
                     "FROM venda_produto vp " +
                     "JOIN venda v ON vp.venda_id = v.id " +
                     "JOIN CustoProduto cp ON vp.produto_id = cp.produto_id " +
                     "WHERE v.empreendimento_id = ? AND v.deleted_at IS NULL AND v.data_venda >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";

        try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, empreendimentoId);
            pstmt.setInt(2, empreendimentoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total_custo");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao calcular custo de produção total.", e);
        }
        return total;
    }

    /**
     * CORRIGIDO: Lista os produtos mais rentáveis com base na margem de lucro (Preço de Venda - Custo).
     * @param empreendimentoId O ID do empreendimento.
     * @param limite           O número máximo de produtos a serem retornados.
     * @return                 Uma lista de mapas, onde cada mapa representa um produto com seu nome e margem.
     */
    public ArrayList<Map<String, Object>> listarProdutosMaisRentaveis(int empreendimentoId, int limite) {
        ArrayList<Map<String, Object>> produtos = new ArrayList<>();
        String sql = "WITH CustoProduto AS (" +
                     "    SELECT " +
                     "        p.id, " +
                     "        p.nome, " +
                     "        p.preco_venda, " +
                     "        ( " +
                     "          COALESCE((SELECT SUM(pi.quantidade_utilizada * " +
                     "              (SELECT ci.preco_unitario FROM compra_insumo ci JOIN compra c ON ci.compra_id = c.id WHERE ci.insumo_id = pi.insumo_id AND ci.quantidade_restante > 0 ORDER BY c.data_compra ASC, c.id ASC LIMIT 1)" +
                     "          ) FROM produto_insumo pi WHERE pi.produto_id = p.id), 0) + " +
                     "          COALESCE((SELECT SUM(pmo.horas_utilizadas * mo.custo_hora) FROM produto_mao_obra pmo JOIN mao_obra mo ON pmo.mao_obra_id = mo.id WHERE pmo.produto_id = p.id), 0)" +
                     "        ) AS custo_total " +
                     "    FROM produto p " +
                     "    WHERE p.empreendimento_id = ? AND p.deleted_at IS NULL " +
                     ") " +
                     "SELECT " +
                     "    id, " +
                     "    nome, " +
                     "    ((preco_venda - custo_total) / preco_venda) * 100 AS margem " +
                     "FROM CustoProduto " +
                     "WHERE preco_venda > 0 " +
                     "ORDER BY margem DESC " +
                     "LIMIT ?";

        try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, empreendimentoId);
            pstmt.setInt(2, limite);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> produto = new HashMap<>();
                produto.put("id", rs.getInt("id"));
                produto.put("nome", rs.getString("nome"));
                produto.put("margem", rs.getDouble("margem"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar produtos mais rentáveis.", e);
        }
        return produtos;
    }

    /**
     * Analisa a variação percentual de vendas dos produtos do mês atual em comparação com o mês anterior.
     * @param empreendimentoId O ID do empreendimento.
     * @param limite           O número máximo de produtos a serem retornados.
     * @return                 Uma lista de mapas, onde cada mapa representa um produto com seu nome e a variação percentual.
     */
    public ArrayList<Map<String, Object>> analisarVariacaoVendas(int empreendimentoId, int limite) {
        ArrayList<Map<String, Object>> variacoes = new ArrayList<>();
        String sql = "WITH VendasPeriodo AS (" +
                     "    SELECT " +
                     "        p.nome, " +
                     "        SUM(CASE WHEN v.data_venda >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) THEN vp.quantidade ELSE 0 END) AS vendas_atual, " +
                     "        SUM(CASE WHEN v.data_venda >= DATE_SUB(CURDATE(), INTERVAL 2 MONTH) AND v.data_venda < DATE_SUB(CURDATE(), INTERVAL 1 MONTH) THEN vp.quantidade ELSE 0 END) AS vendas_anterior " +
                     "    FROM venda_produto vp " +
                     "    JOIN venda v ON vp.venda_id = v.id " +
                     "    JOIN produto p ON vp.produto_id = p.id " +
                     "    WHERE v.empreendimento_id = ? AND v.deleted_at IS NULL " +
                     "    GROUP BY p.nome " +
                     ") " +
                     "SELECT " +
                     "    nome, " +
                     "    ((vendas_atual - vendas_anterior) / vendas_anterior) * 100 AS variacao " +
                     "FROM VendasPeriodo " +
                     "WHERE vendas_anterior > 0 " +
                     "ORDER BY ABS(variacao) DESC " +
                     "LIMIT ?";

        try (Connection con = ConnectionFactory.conectar(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, empreendimentoId);
            pstmt.setInt(2, limite);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> variacao = new HashMap<>();
                variacao.put("nome", rs.getString("nome"));
                variacao.put("variacao", rs.getDouble("variacao"));
                variacoes.add(variacao);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao analisar variação de vendas.", e);
        }
        return variacoes;
    }
}