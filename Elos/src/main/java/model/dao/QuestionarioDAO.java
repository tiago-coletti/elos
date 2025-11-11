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

import model.entity.Pergunta;
import model.entity.Questionario;
import util.ConnectionFactory;

public class QuestionarioDAO {

    private static final Logger logger = Logger.getLogger(QuestionarioDAO.class.getName());

    /**
     * Obtém um questionário pelo nome, ou o cria caso não exista.
     * Também carrega a lista de perguntas associadas a ele. (Usado pelo Admin)
     * @param nomeQuestionario (String) O nome exato do questionário (ex: "Plano de Negócios").
     * @return (Questionario) Um objeto Questionario, preenchido com sua List<Pergunta>.
     */
    public Questionario obterOuCriarQuestionarioComPerguntas(String nomeQuestionario) {
        Questionario questionario = null;
        String sqlSelect = "SELECT * FROM questionario WHERE nome = ? AND deleted_at IS NULL";
        String sqlInsert = "INSERT INTO questionario (nome, descricao) VALUES (?, ?)";
        
        try (Connection con = ConnectionFactory.conectar()) {
            
            try (PreparedStatement pstSelect = con.prepareStatement(sqlSelect)) {
                pstSelect.setString(1, nomeQuestionario);
                ResultSet rs = pstSelect.executeQuery();
                
                if (rs.next()) {
                    questionario = new Questionario(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("deleted_at")
                    );
                }
            }

            if (questionario == null) {
                try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    pstInsert.setString(1, nomeQuestionario);
                    pstInsert.setString(2, "Gerado automaticamente pelo sistema.");
                    pstInsert.executeUpdate();
                    
                    ResultSet rsKeys = pstInsert.getGeneratedKeys();
                    if (rsKeys.next()) {
                        questionario = new Questionario(
                            rsKeys.getLong(1), nomeQuestionario, "Gerado automaticamente pelo sistema.", 
                            null, null, null 
                        );
                    }
                }
            }

            if (questionario != null) {
                List<Pergunta> perguntas = new ArrayList<>();
                String sqlPerguntas = "SELECT * FROM pergunta WHERE questionario_id = ? AND deleted_at IS NULL ORDER BY ordem";
                
                try (PreparedStatement pstPerguntas = con.prepareStatement(sqlPerguntas)) {
                    pstPerguntas.setLong(1, questionario.getId());
                    ResultSet rsPerguntas = pstPerguntas.executeQuery();
                    
                    while (rsPerguntas.next()) {
                        Pergunta p = new Pergunta(
                            rsPerguntas.getLong("id"),
                            rsPerguntas.getLong("questionario_id"),
                            rsPerguntas.getString("texto_pergunta"),
                            rsPerguntas.getString("tipo_pergunta"),
                            rsPerguntas.getInt("ordem"),
                            rsPerguntas.getString("created_at"),
                            rsPerguntas.getString("updated_at"),
                            rsPerguntas.getString("deleted_at")
                        );
                        perguntas.add(p);
                    }
                }
                questionario.setPerguntas(perguntas);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter ou criar questionário: " + nomeQuestionario, e);
        }
        
        return questionario;
    }

    /**
     * Busca uma única pergunta pelo seu ID. (Usado pelo Admin)
     * @param perguntaId (long) O ID da pergunta.
     * @return (Pergunta) O objeto Pergunta preenchido, ou null se não for encontrado.
     */
    public Pergunta obterPerguntaPorId(long perguntaId) {
        String sql = "SELECT * FROM pergunta WHERE id = ? AND deleted_at IS NULL";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setLong(1, perguntaId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new Pergunta(
                    rs.getLong("id"),
                    rs.getLong("questionario_id"),
                    rs.getString("texto_pergunta"),
                    rs.getString("tipo_pergunta"),
                    rs.getInt("ordem"),
                    rs.getString("created_at"),
                    rs.getString("updated_at"),
                    rs.getString("deleted_at")
                );
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter pergunta por ID: " + perguntaId, e);
        }
        return null;
    }

    /**
     * Insere uma nova pergunta no banco de dados, deslocando outras perguntas se necessário. (Usado pelo Admin)
     * @param pergunta (Pergunta) O objeto Pergunta preenchido (com questionarioId, texto, tipo, ordem).
     * @return (boolean) true se a inclusão foi bem-sucedida, false caso contrário.
     */
    public boolean incluirPergunta(Pergunta pergunta) {
        String sqlUpdate = "UPDATE pergunta SET ordem = ordem + 1 WHERE questionario_id = ? AND ordem >= ? AND deleted_at IS NULL";
        String sqlInsert = "INSERT INTO pergunta (questionario_id, texto_pergunta, tipo_pergunta, ordem) VALUES (?, ?, ?, ?)";
        
        Connection con = null;
        try {
            con = ConnectionFactory.conectar();
            con.setAutoCommit(false); 

            try (PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate)) {
                pstUpdate.setLong(1, pergunta.getQuestionarioId());
                pstUpdate.setInt(2, pergunta.getOrdem());
                pstUpdate.executeUpdate(); 
            }
            
            try (PreparedStatement pstInsert = con.prepareStatement(sqlInsert)) {
                pstInsert.setLong(1, pergunta.getQuestionarioId());
                pstInsert.setString(2, pergunta.getTextoPergunta());
                pstInsert.setString(3, pergunta.getTipoPergunta());
                pstInsert.setInt(4, pergunta.getOrdem());
                
                int rowsAffected = pstInsert.executeUpdate();
                
                if (rowsAffected > 0) {
                    con.commit(); 
                    return true;
                } else {
                    con.rollback(); 
                    return false;
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao incluir nova pergunta com deslocamento", e);
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Erro ao reverter transação", ex); } }
            return false;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { logger.log(Level.SEVERE, "Erro ao fechar conexão", e); } }
        }
    }

    /**
     * Atualiza uma pergunta existente, ajustando a ordem das outras se necessário. (Usado pelo Admin)
     * @param pergunta (Pergunta) O objeto Pergunta com ID e dados novos (texto, tipo, ordem).
     * @return (boolean) true se a atualização foi bem-sucedida.
     */
    public boolean atualizarPergunta(Pergunta pergunta) {
        Connection con = null;
        try {
            con = ConnectionFactory.conectar();
            con.setAutoCommit(false);

            Pergunta perguntaAntiga = obterPerguntaPorId(pergunta.getId());
            if (perguntaAntiga == null) return false;
            
            int antigaOrdem = perguntaAntiga.getOrdem();
            int novaOrdem = pergunta.getOrdem();

            if (novaOrdem != antigaOrdem) {
                String sqlMoveOut = "UPDATE pergunta SET ordem = -1 WHERE id = ?";
                try (PreparedStatement pst = con.prepareStatement(sqlMoveOut)) {
                    pst.setLong(1, pergunta.getId());
                    pst.executeUpdate();
                }

                if (novaOrdem < antigaOrdem) {
                    String sqlShiftUp = "UPDATE pergunta SET ordem = ordem + 1 WHERE questionario_id = ? AND ordem >= ? AND ordem < ? AND deleted_at IS NULL";
                    try (PreparedStatement pst = con.prepareStatement(sqlShiftUp)) {
                        pst.setLong(1, pergunta.getQuestionarioId());
                        pst.setInt(2, novaOrdem);
                        pst.setInt(3, antigaOrdem);
                        pst.executeUpdate();
                    }
                } else {
                    String sqlShiftDown = "UPDATE pergunta SET ordem = ordem - 1 WHERE questionario_id = ? AND ordem > ? AND ordem <= ? AND deleted_at IS NULL";
                     try (PreparedStatement pst = con.prepareStatement(sqlShiftDown)) {
                        pst.setLong(1, pergunta.getQuestionarioId());
                        pst.setInt(2, antigaOrdem);
                        pst.setInt(3, novaOrdem);
                        pst.executeUpdate();
                    }
                }
            }
            
            String sqlUpdateFinal = "UPDATE pergunta SET texto_pergunta = ?, tipo_pergunta = ?, ordem = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlUpdateFinal)) {
                pst.setString(1, pergunta.getTextoPergunta());
                pst.setString(2, pergunta.getTipoPergunta());
                pst.setInt(3, novaOrdem);
                pst.setLong(4, pergunta.getId());
                pst.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar pergunta com deslocamento", e);
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Erro ao reverter transação", ex); } }
            return false;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { logger.log(Level.SEVERE, "Erro ao fechar conexão", e); } }
        }
    }

    /**
     * Exclui (soft delete) uma pergunta e reordena as perguntas seguintes. (Usado pelo Admin)
     * @param perguntaId (long) O ID da pergunta a ser excluída.
     * @return (boolean) true se a exclusão foi bem-sucedida.
     */
    public boolean excluirPergunta(long perguntaId) {
        Connection con = null;
        try {
            con = ConnectionFactory.conectar();
            con.setAutoCommit(false);

            Pergunta pergunta = obterPerguntaPorId(perguntaId);
            if (pergunta == null) return false;

            String sqlDelete = "UPDATE pergunta SET deleted_at = NOW() WHERE id = ?";
            try (PreparedStatement pst = con.prepareStatement(sqlDelete)) {
                pst.setLong(1, perguntaId);
                pst.executeUpdate();
            }
            
            String sqlShift = "UPDATE pergunta SET ordem = ordem - 1 WHERE questionario_id = ? AND ordem > ? AND deleted_at IS NULL";
            try (PreparedStatement pst = con.prepareStatement(sqlShift)) {
                pst.setLong(1, pergunta.getQuestionarioId());
                pst.setInt(2, pergunta.getOrdem());
                pst.executeUpdate();
            }

            con.commit();
            return true;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao excluir pergunta com deslocamento", e);
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Erro ao reverter transação", ex); } }
            return false;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { logger.log(Level.SEVERE, "Erro ao fechar conexão", e); } }
        }
    }

    /**
     * Obtém um mapa de respostas (perguntaId -> textoResposta) para um empreendimento.
     * @param questionarioId (long) O ID do questionário.
     * @param empreendimentoId (int) O ID do empreendimento.
     * @return (Map<Long, String>) Um mapa contendo as respostas já salvas.
     */
    public Map<Long, String> obterMapaRespostas(long questionarioId, int empreendimentoId) {
        Map<Long, String> respostas = new HashMap<>();
        String sql = "SELECT p.id AS pergunta_id, r.texto_resposta " +
                     "FROM pergunta p " +
                     "LEFT JOIN resposta r ON p.id = r.pergunta_id AND r.empreendimento_id = ? AND r.deleted_at IS NULL " +
                     "WHERE p.questionario_id = ? AND p.deleted_at IS NULL";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, empreendimentoId);
            pst.setLong(2, questionarioId);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getString("texto_resposta") != null) {
                    respostas.put(rs.getLong("pergunta_id"), rs.getString("texto_resposta"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter mapa de respostas para empreendimento " + empreendimentoId, e);
        }
        return respostas;
    }

    /**
     * Salva (Insere ou Atualiza) um lote de respostas para um empreendimento.
     * @param empreendimentoId (int) O ID do empreendimento.
     * @param respostas (Map<Long, String>) O mapa de (perguntaId -> textoResposta) a ser salvo.
     * @return (boolean) true se todas as operações foram bem-sucedidas.
     */
    public boolean salvarRespostas(int empreendimentoId, Map<Long, String> respostas) {
        String sqlUpsert = "INSERT INTO resposta (empreendimento_id, pergunta_id, texto_resposta) " +
                           "VALUES (?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE texto_resposta = VALUES(texto_resposta), updated_at = NOW()";
        
        Connection con = null;
        try {
            con = ConnectionFactory.conectar();
            con.setAutoCommit(false);

            try (PreparedStatement pst = con.prepareStatement(sqlUpsert)) {
                for (Map.Entry<Long, String> entry : respostas.entrySet()) {
                    long perguntaId = entry.getKey();
                    String textoResposta = entry.getValue();

                    pst.setInt(1, empreendimentoId);
                    pst.setLong(2, perguntaId);
                    pst.setString(3, textoResposta);
                    
                    pst.addBatch();
                }
                pst.executeBatch();
            }
            
            con.commit();
            return true;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro na transação ao salvar respostas", e);
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { logger.log(Level.SEVERE, "Erro ao reverter transação", ex); } }
            return false;
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { logger.log(Level.SEVERE, "Erro ao fechar conexão", e); } }
        }
    }
}