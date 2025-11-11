package model.dao;

import java.sql.Connection;
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

import model.entity.Admin;
import util.ConnectionFactory;

public class AdminDAO {

    private static final Logger logger = Logger.getLogger(AdminDAO.class.getName());

    /**
     * Obtém o ID de um admin com base em seu login ou email.
     * @param loginOuEmail (String) O login ou o email do admin.
     * @return (long) O ID do admin ou -1L caso não seja encontrado.
     */
    public long obterId(String loginOuEmail) {
        String sqlObterIdAdmin = "SELECT id FROM admin WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlObterIdAdmin)) {
            pst.setString(1, loginOuEmail);
            pst.setString(2, loginOuEmail);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao obter ID do admin para o login: " + loginOuEmail, e);
        }
        return -1L;
    }

    /**
     * Verifica se um admin (identificado pelo login ou email) existe no banco de dados.
     * @param loginOuEmail (String) O login ou o email a ser verificado.
     * @return (boolean) true se o admin existe, false caso contrário.
     */
    public boolean verificarUsuarioExistente(String loginOuEmail) {
        String sql = "SELECT 1 FROM admin WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, loginOuEmail);
            pst.setString(2, loginOuEmail);

            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao verificar a existência do admin: " + loginOuEmail, e);
        }
        return false;
    }

    /**
     * Obtém o hash da senha armazenada no banco de dados para um admin.
     * @param loginOuEmail (String) O login ou o email do admin.
     * @return (String) A senha (hash) do admin ou null caso não seja encontrada.
     */
    public String obterSenhaPorLoginOuEmail(String loginOuEmail) {
        String sql = "SELECT senha FROM admin WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, loginOuEmail);
            pst.setString(2, loginOuEmail);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("senha");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter hash da senha para o admin: " + loginOuEmail, e);
        }
        return null;
    }

    /**
     * Insere um novo admin no banco de dados.
     * @param admin (Admin) O objeto Admin preenchido a ser incluído.
     * @return (boolean) true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean incluirAdmin(Admin admin) {
        String sqlIncluirAdmin = 
            "INSERT INTO admin (nome, email, senha, login) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlIncluirAdmin,
                        Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, admin.getNome());
            pst.setString(2, admin.getEmail());
            pst.setString(3, admin.getSenha());
            pst.setString(4, admin.getLogin());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setId(rs.getInt(1)); // Correção: de getInt para getLong
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao incluir admin", e);
            return false;
        }
    }

    /**
     * Busca os dados dos cards para o dashboard principal (Empreendimentos PADRAO).
     * @return (List<Map<String, Object>>) Uma lista de Maps, onde cada Map representa um card de empreendimento.
     */
    public List<Map<String, Object>> obterDadosCardsDashboard() {
        List<Map<String, Object>> cards = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    e.id, e.nome, e.saldo, " +
                     "    COUNT(DISTINCT i.id) AS total_insumos, " +
                     "    COUNT(DISTINCT c.id) AS total_compras, " +
                     "    COUNT(DISTINCT v.id) AS total_vendas " +
                     "FROM empreendimento e " +
                     "LEFT JOIN insumo i ON e.id = i.empreendimento_id AND i.deleted_at IS NULL " +
                     "LEFT JOIN compra c ON e.id = c.empreendimento_id AND c.deleted_at IS NULL " +
                     "LEFT JOIN venda v ON e.id = v.empreendimento_id AND v.deleted_at IS NULL " +
                     "WHERE e.tipo = 'PADRAO' AND e.deleted_at IS NULL " +
                     "GROUP BY e.id, e.nome, e.saldo " +
                     "ORDER BY e.nome";

        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> card = new HashMap<>();
                card.put("id", rs.getInt("id"));
                card.put("nome", rs.getString("nome"));
                card.put("saldo", rs.getDouble("saldo"));
                card.put("totalInsumos", rs.getInt("total_insumos"));
                card.put("totalCompras", rs.getInt("total_compras"));
                card.put("totalVendas", rs.getInt("total_vendas"));
                cards.add(card);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar dados do dashboard de admin", e);
        }
        return cards;
    }
    
    /**
     * Busca todos os semestres distintos cadastrados para os alunos.
     * @return (List<String>) Uma lista de semestres (ex: "2024/2").
     */
    public List<String> obterSemestresAlunos() {
        List<String> semestres = new ArrayList<>();
        String sql = "SELECT DISTINCT ano_semestre FROM empreendimento_aluno ORDER BY ano_semestre DESC";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                semestres.add(rs.getString("ano_semestre"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao buscar lista de semestres de alunos", e);
        }
        return semestres;
    }

    /**
     * Busca os dados dos cards para o dashboard IFSC (Empreendimentos ALUNO) com filtros.
     * @param filtroSemestre (String) O semestre para filtrar (ex: "2024/2") ou null/vazio.
     * @param filtroCurso (String) O curso para filtrar (ex: "ENERGIA_RENOVAVEIS") ou null/vazio.
     * @return (List<Map<String, Object>>) Uma lista de Maps, onde cada Map representa um card de empreendimento.
     */
    public List<Map<String, Object>> obterDadosCardsIfsc(String filtroSemestre, String filtroCurso) {
        List<Map<String, Object>> cards = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    e.id, e.nome, e.saldo, " +
            "    COUNT(DISTINCT i.id) AS total_insumos, " +
            "    COUNT(DISTINCT c.id) AS total_compras, " +
            "    COUNT(DISTINCT v.id) AS total_vendas " +
            "FROM empreendimento e " +
            "LEFT JOIN insumo i ON e.id = i.empreendimento_id AND i.deleted_at IS NULL " +
            "LEFT JOIN compra c ON e.id = c.empreendimento_id AND c.deleted_at IS NULL " +
            "LEFT JOIN venda v ON e.id = v.empreendimento_id AND v.deleted_at IS NULL " +
            "LEFT JOIN empreendimento_aluno ea ON e.id = ea.empreendimento_id " +
            "LEFT JOIN aluno a ON ea.aluno_id = a.id " +
            "WHERE e.tipo = 'ALUNO' AND e.deleted_at IS NULL "
        );

        if (filtroSemestre != null && !filtroSemestre.isEmpty()) {
            sql.append("AND ea.ano_semestre = ? ");
            params.add(filtroSemestre);
        }

        if (filtroCurso != null && !filtroCurso.isEmpty()) {
            sql.append("AND a.curso = ? ");
            params.add(filtroCurso);
        }

        sql.append("GROUP BY e.id, e.nome, e.saldo ORDER BY e.nome");

        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> card = new HashMap<>();
                    card.put("id", rs.getInt("id"));
                    card.put("nome", rs.getString("nome"));
                    card.put("saldo", rs.getDouble("saldo"));
                    card.put("totalInsumos", rs.getInt("total_insumos"));
                    card.put("totalCompras", rs.getInt("total_compras"));
                    card.put("totalVendas", rs.getInt("total_vendas"));
                    cards.add(card);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar dados do dashboard IFSC de admin", e);
        }
        return cards;
    }
}