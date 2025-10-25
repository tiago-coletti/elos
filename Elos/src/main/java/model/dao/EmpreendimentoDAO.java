package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Aluno;
import model.entity.Empreendimento;
import model.entity.EmpreendimentoAluno;
import util.ConnectionFactory;

public class EmpreendimentoDAO {
	
	private static final Logger logger = Logger.getLogger(EmpreendimentoDAO.class.getName());
	
	/**
     * Obtém o ID de um empreendimento com base em seu login ou email.
     * @param login Login ou Email do empreendimento.
     * @return ID do empreendimento ou -1 caso não seja encontrado.
     */
    public int obterId(String loginOuEmail) {
        String sqlObterIdEmpreendimento = "SELECT id FROM empreendimento WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
                PreparedStatement pst = con.prepareStatement(sqlObterIdEmpreendimento)) {
            pst.setString(1, loginOuEmail);
            pst.setString(2, loginOuEmail);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao obter ID do empreendimento para o login: " + loginOuEmail, e);
        }
        return -1;
    }
    
    /**
     * Verifica se um usuário (identificado pelo login ou email) existe no banco de dados.
     * @param loginOuEmail O login ou o email a ser verificado.
     * @return true se o usuário existe, false caso contrário.
     */
    public boolean verificarUsuarioExistente(String loginOuEmail) {
        String sql = "SELECT 1 FROM empreendimento WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
               
               pst.setString(1, loginOuEmail);
               pst.setString(2, loginOuEmail);
               
               ResultSet rs = pst.executeQuery();
               return rs.next(); 
           } catch (SQLException e) {
               logger.log(Level.WARNING, "Erro ao verificar a existência do usuário: " + loginOuEmail, e);
           }
           return false; 
    }
    
    /**
     * Obtém o hash da senha armazenada no banco de dados para um usuário (pelo login ou email).
     * @param loginOuEmail O login ou o email do empreendimento.
     * @return A senha do empreendimento ou null caso não seja encontrada.
     */
    public String obterSenhaPorLoginOuEmail(String loginOuEmail) {
        String sql = "SELECT senha FROM empreendimento WHERE login = ? OR email = ?";
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, loginOuEmail);
            pst.setString(2, loginOuEmail);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("senha");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter hash da senha para o usuário: " + loginOuEmail, e);
        }
        return null; 
    }
    
    /**
     * Insere um novo empreendimento no banco de dados.
     * @param empreendimento O empreendimento a ser incluído.
     * @return true se o cadastro foi bem-sucedido.
     */
    public boolean incluirEmpreendimento(Empreendimento empreendimento) {
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

    /**
     * Obtém os dados dos alunos de um empreendimento.
     * @param id ID do empreendimento logado.
     * @return List Lista com os alunos.
     */
    public List<Aluno> obterAlunosPorEmpreendimentoId(int empreendimentoId) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.* FROM aluno a " +
                     "INNER JOIN empreendimento_aluno ea ON a.id = ea.aluno_id " +
                     "WHERE ea.empreendimento_id = ?";

        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, empreendimentoId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Aluno aluno = new Aluno(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("matricula"),
                    rs.getString("curso")
                    // Adicione outros campos do aluno se houver
                );
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter alunos para o empreendimento ID: " + empreendimentoId, e);
        }
        return alunos;
    }
    
    /**
     * Obtém os dados completos de um empreendimento (e dados de aluno se for o caso)
     * com base no ID.
     * @param id ID do empreendimento logado.
     * @return Objeto Empreendimento preenchido ou null.
     */
    public Empreendimento obterEmpreendimentoCompletoPorId(int id) {
        String sql = "SELECT * FROM empreendimento WHERE id = ?";
        Empreendimento empreendimento = null;

        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    empreendimento = new Empreendimento(
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

                    // Se for um empreendimento de alunos, busca os dados específicos
                    if (empreendimento.isAluno()) {
                        // Busca a lista de alunos e anexa ao objeto
                        List<Aluno> alunos = obterAlunosPorEmpreendimentoId(id);
                        empreendimento.setAlunos(alunos);

                        // Lógica para pegar ano/semestre e curso (do primeiro aluno, por exemplo)
                        if (!alunos.isEmpty()) {
                            try (PreparedStatement pstAlunoInfo = con.prepareStatement(
                                 "SELECT ano_semestre FROM empreendimento_aluno WHERE empreendimento_id = ? LIMIT 1")) {
                                pstAlunoInfo.setInt(1, id);
                                ResultSet rsAlunoInfo = pstAlunoInfo.executeQuery();
                                if (rsAlunoInfo.next()) {
                                    empreendimento.setAnoSemestre(rsAlunoInfo.getString("ano_semestre"));
                                }
                            }

                            // Mapeia o ENUM do curso do primeiro aluno para o ID do JSP
                            String cursoEnum = alunos.get(0).getCurso();
                            int cursoId = 0;
                            if ("DESENVOLVIMENTO_SISTEMAS".equals(cursoEnum)) {
                                cursoId = 1;
                            } else if ("ENERGIA_RENOVAVEIS".equals(cursoEnum)) {
                                cursoId = 2;
                            }
                            empreendimento.setCursoGeralId(cursoId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao obter empreendimento completo por ID: " + id, e);
        }

        return empreendimento;
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
     * Atualiza os dados de um único aluno no banco de dados.
     * @param aluno O objeto Aluno contendo o ID e os novos dados (nome, matricula).
     * @return true se a atualização foi bem-sucedida.
     */
    public boolean atualizarAluno(Aluno aluno) {
        String sql = "UPDATE aluno SET nome = ?, matricula = ? WHERE id = ?";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, aluno.getNome());
            pst.setString(2, aluno.getMatricula());
            pst.setInt(3, aluno.getId());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar aluno ID: " + aluno.getId(), e);
            return false;
        }
    }
    
    /**
     * Remove a associação entre um empreendimento e um aluno.
     * @param empreendimentoId O ID do empreendimento.
     * @param alunoId O ID do aluno a ser desvinculado.
     * @return true se a remoção for bem-sucedida.
     */
    public boolean removerAssociacaoAluno(int empreendimentoId, int alunoId) {
        String sql = "DELETE FROM empreendimento_aluno WHERE empreendimento_id = ? AND aluno_id = ?";
        
        try (Connection con = ConnectionFactory.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, empreendimentoId);
            pst.setInt(2, alunoId);
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao remover associação para empreendimento ID: " + empreendimentoId + " e aluno ID: " + alunoId, e);
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