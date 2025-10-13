package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Aluno;
import model.entity.Empreendimento;
import model.entity.EmpreendimentoAluno;
import util.ConnectionFactory;

public class EmpreendimentoDAO {
	
	private static final Logger logger = Logger.getLogger(EmpreendimentoDAO.class.getName());
	
	/**
     * Obtém o ID de um empreendimento com base em seu login (campo 'login' na tabela).
     * @param login Login do empreendimento.
     * @return ID do empreendimento ou -1 caso não seja encontrado.
     */
    public int obterId(String login) {
        String sqlObterIdEmpreendimento = "SELECT id FROM empreendimento WHERE login = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlObterIdEmpreendimento)) {
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao obter ID do empreendimento para o login: " + login, e);
        }
        return -1;
    }
    
    /**
     * Verifica se o login (campo 'login' na tabela) existe no banco de dados.
     * @param login Login a ser verificado.
     * @return true se o login existe, false caso contrário.
     */
    public boolean verificarLogin(String login) {
        String sqlVerificarLogin = "SELECT 1 FROM empreendimento WHERE login = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlVerificarLogin)) {
               pst.setString(1, login);
               ResultSet rs = pst.executeQuery();
               return rs.next();
           } catch (SQLException e) {
               logger.log(Level.WARNING, "Erro ao verificar o login: " + login, e);
           }
           return false;
    }
    
    /**
     * Obtém o hash da senha armazenada no banco de dados para um login.
     * @param login Login do empreendimento.
     * @return A senha do empreendimento ou null caso não seja encontrada.
     */
    public String obterSenhaPorLogin(String login) {
        String sqlObterSenhaPorLogin = "SELECT senha FROM empreendimento WHERE login = ?";
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sqlObterSenhaPorLogin)) {
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("senha");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter hash da senha para o login: " + login, e);
        }
        return null;
    }
    
    /**
     * Insere um novo empreendimento no banco de dados.
     * @param empreendimento O empreendimento a ser incluído.
     * @return true se o cadastro foi bem-sucedido.
     */
    public boolean incluirEmpreendimento(Empreendimento empreendimento) {
        // Colunas ajustadas: login, numero_telefone, cidade
        String sqlIncluirEmpreendimento = 
        	"INSERT INTO empreendimento (nome, email, senha, login, tipo, numero_telefone, cidade) " +
        	"VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlIncluirEmpreendimento, 
                    Statement.RETURN_GENERATED_KEYS)) { 
            
            // 1. Informações Comuns
            pst.setString(1, empreendimento.getNome());
            pst.setString(2, empreendimento.getEmail());
            pst.setString(3, empreendimento.getSenha());
            
            // 2. Novos campos ajustados
            pst.setString(4, empreendimento.getLogin());
            pst.setString(5, empreendimento.getTipo());
            pst.setString(6, empreendimento.getNumeroTelefone());
            pst.setString(7, empreendimento.getCidade());
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                // Obtém o ID gerado para uso subsequente (e.g., alunos)
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        empreendimento.setId(rs.getInt(1)); 
                    }
                }
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao incluir empreendimento", e);
            return false;
        }
    }

    /**
     * Insere um novo aluno no banco de dados.
     * Colunas ajustadas: nome, matricula, curso (ENUM)
     * @param aluno O aluno a ser incluído.
     * @return O ID do aluno inserido ou -1 em caso de falha.
     */
    public int incluirAluno(Aluno aluno) {
        String sqlIncluirAluno = "INSERT INTO aluno (nome, matricula, curso) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlIncluirAluno, 
                    Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, aluno.getNome());
            pst.setString(2, aluno.getMatricula());
            pst.setString(3, aluno.getCurso()); 
            pst.executeUpdate();
            
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao incluir aluno: " + aluno.getNome(), e);
        }
        return -1;
    }

    /**
     * Associa um aluno a um empreendimento solidário.
     * @param empreendimentoAluno O objeto de ligação.
     * @return true se a inclusão for bem-sucedida.
     */
    public boolean incluirEmpreendimentoAluno(EmpreendimentoAluno empreendimentoAluno) {
        String sqlIncluirEmpreendimentoAluno = 
        	"INSERT INTO empreendimento_aluno (empreendimento_id, aluno_id, ano_semestre) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlIncluirEmpreendimentoAluno)) {
            pst.setInt(1, empreendimentoAluno.getEmpreendimentoId());
            pst.setInt(2, empreendimentoAluno.getAlunoId());
            pst.setString(3, empreendimentoAluno.getAnoSemestre());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao associar aluno ao empreendimento. Empreendimento ID: " 
            	+ empreendimentoAluno.getEmpreendimentoId() + ", Aluno ID: " + empreendimentoAluno.getAlunoId(), e);
            return false;
        }
    }

 // Dentro de model.dao.EmpreendimentoDAO

    /**
     * Obtém os dados completos de um empreendimento (e dados de aluno se for o caso)
     * com base no ID.
     * @param id ID do empreendimento logado.
     * @return Objeto Empreendimento preenchido ou null.
     */
    public Empreendimento obterEmpreendimentoCompletoPorId(int id) {
        // LEFT JOIN para obter dados de ALUNO: ano_semestre e curso (apenas o primeiro aluno, 
        // assumindo que é o dado do grupo)
        String sql = "SELECT e.*, ea.ano_semestre, a.curso AS curso_aluno_enum " +
                     "FROM empreendimento e " +
                     "LEFT JOIN empreendimento_aluno ea ON e.id = ea.empreendimento_id " +
                     "LEFT JOIN aluno a ON ea.aluno_id = a.id " +
                     "WHERE e.id = ? LIMIT 1";

        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // 1. Cria o objeto Empreendimento
                    Empreendimento e = new Empreendimento(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("login"),
                        rs.getString("tipo"),
                        rs.getString("numero_telefone"),
                        rs.getString("cidade"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("deleted_at")
                    );

                    // 2. Adiciona dados específicos de Aluno (se aplicável)
                    if (e.isAluno()) {
                        String cursoEnum = rs.getString("curso_aluno_enum");
                        int cursoId = 0;
                        // Mapeia o ENUM do banco para o ID usado no JSP (login.html e conta.jsp)
                        if ("DESENVOLVIMENTO_SISTEMAS".equals(cursoEnum)) {
                            cursoId = 1;
                        } else if ("ENERGIA_RENOVAVEIS".equals(cursoEnum)) {
                            cursoId = 2;
                        }
                        e.setCursoGeralId(cursoId);
                        e.setAnoSemestre(rs.getString("ano_semestre"));
                    }

                    return e;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter empreendimento completo por ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Atualiza os dados principais (incluindo senha, se alterada) de um empreendimento.
     * @param empreendimento O objeto Empreendimento com os dados atualizados.
     * @return true se a atualização foi bem-sucedida.
     */
    public boolean atualizarEmpreendimento(Empreendimento empreendimento) {
        // Inclui a atualização do campo updated_at para a data/hora atual
        String sql = "UPDATE empreendimento SET nome=?, email=?, senha=?, login=?, numero_telefone=?, cidade=?, updated_at=NOW() WHERE id=?";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, empreendimento.getNome());
            pst.setString(2, empreendimento.getEmail());
            pst.setString(3, empreendimento.getSenha());
            pst.setString(4, empreendimento.getLogin());
            pst.setString(5, empreendimento.getNumeroTelefone());
            pst.setString(6, empreendimento.getCidade());
            pst.setInt(7, empreendimento.getId());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar empreendimento ID: " + empreendimento.getId(), e);
            return false;
        }
    }
    
    /**
     * Atualiza o curso de todos os alunos associados e o ano/semestre do grupo empreendimento-aluno, usando Transação.
     * @param empreendimentoId ID do empreendimento.
     * @param cursoEnum O ENUM do curso (ex: DESENVOLVIMENTO_SISTEMAS).
     * @param anoSemestre O novo ano/semestre.
     * @return true se as atualizações foram bem-sucedidas.
     */
    public boolean atualizarEmpreendimentoAlunoInfo(int empreendimentoId, String cursoEnum, String anoSemestre) {
        String sqlUpdateAnoSemestre = "UPDATE empreendimento_aluno SET ano_semestre=? WHERE empreendimento_id=?";
        String sqlUpdateCursoAluno = "UPDATE aluno a " +
                                     "INNER JOIN empreendimento_aluno ea ON a.id = ea.aluno_id " +
                                     "SET a.curso=? " +
                                     "WHERE ea.empreendimento_id=?";

        // Declara a variável con para ser acessível no catch
        Connection con = null; 

        try {
            // Inicializa a conexão e a usa fora do try-with-resources aninhado
            con = ConnectionFactory.conectar();
            con.setAutoCommit(false); // Inicia a transação
            
            // --- 1. Atualiza Ano/Semestre ---
            try (PreparedStatement pst1 = con.prepareStatement(sqlUpdateAnoSemestre)) {
                pst1.setString(1, anoSemestre);
                pst1.setInt(2, empreendimentoId);
                pst1.executeUpdate();
            }

            // --- 2. Atualiza Curso do Aluno ---
            try (PreparedStatement pst2 = con.prepareStatement(sqlUpdateCursoAluno)) {
                pst2.setString(1, cursoEnum);
                pst2.setInt(2, empreendimentoId);
                pst2.executeUpdate();
            }
            
            con.commit(); // Confirma as alterações
            return true;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro na transação de atualização de Empreendimento Aluno ID: " + empreendimentoId, e);
            
            // Agora con está no escopo, podemos tentar o rollback
            if (con != null) {
                try {
                    logger.log(Level.INFO, "Tentando Rollback na transação do Empreendimento ID: " + empreendimentoId);
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Erro ao tentar Rollback!", rollbackEx);
                }
            }
            return false;
            
        } finally {
            // Garante que a conexão seja fechada, mesmo em caso de sucesso ou falha
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restaura o auto-commit
                    con.close();
                } catch (SQLException closeEx) {
                    logger.log(Level.SEVERE, "Erro ao fechar a conexão no finally!", closeEx);
                }
            }
        }
    }
}