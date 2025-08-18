package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.entity.Empreendimento;
import util.ConnectionFactory;

public class EmpreendimentoDAO {
	
	private static final Logger logger = Logger.getLogger(EmpreendimentoDAO.class.getName());
	
    /**
     * Obtém o ID de um empreendimento com base em seu login.
     * @param login Login do empreendimento.
     * @return ID do empreendimento ou -1 caso não seja encontrado.
     */
	public int obterId(String login) {
		String sqlObterIdEmpreendimento = "SELECT id FROM empreendimento WHERE email = ?";
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
     * Verifica se o login existe no banco de dados.
     * @param login Login a ser verificado.
     * @return true se o login existe, false caso contrário.
     */
	public boolean verificarLogin(String login) {
		String sqlVerificarLogin = "SELECT 1 FROM empreendimento WHERE email = ?";
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
        String sqlObterSenhaPorLogin = "SELECT senha FROM empreendimento WHERE email = ?";
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
     */
	public boolean incluirEmpreendimento(Empreendimento empreendimento) {
		String sqlIncluirEmpreendimento = "INSERT INTO empreendimento (nome, email, senha) VALUES (?, ?, ?)";
		try (Connection con = ConnectionFactory.conectar();
				PreparedStatement pst = con.prepareStatement(sqlIncluirEmpreendimento)) {
			pst.setString(1, empreendimento.getNome());
			pst.setString(2, empreendimento.getEmail());
			pst.setString(3, empreendimento.getSenha());
			pst.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao incluir empreendimento", e);
			return false;
		}
	}
}