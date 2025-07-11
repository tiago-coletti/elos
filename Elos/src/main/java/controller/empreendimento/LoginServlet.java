package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.EmpreendimentoDAO;
import util.PasswordUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
	
	//Instanciando os DAO's necessários
	EmpreendimentoDAO empreendimentoDAO = new EmpreendimentoDAO();

       
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(30 * 60); 
		response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; HttpOnly; Secure");
		
		//Obtendo os parâmetros de requisição
		String login = request.getParameter("login");
		String senha = request.getParameter("senha");
		
		//Verificando se os parâmetros são nulos/vazios
		if(login == null || login == "" || senha == null || senha == "") {
			logger.log(Level.SEVERE, "Falha no Login: Matrícula ou senha estão vazias ou nulas.");
			response.sendRedirect("");
		}
		
		// Verificando se o login é válido usando o DAO
		if (empreendimentoDAO.verificarLogin(login)) {
            // Obter o hash da senha
            String storedHash = empreendimentoDAO.obterSenhaPorLogin(login);
            if (storedHash != null && PasswordUtils.checkPassword(senha, storedHash)) {
                // Autenticação bem-sucedida
                int idEmpreendimento = empreendimentoDAO.obterId(login);
                request.getSession().setAttribute("idEmpreendimento", idEmpreendimento);

                // Redirecionar para a página inicial
                RequestDispatcher rd = request.getRequestDispatcher("");
                rd.forward(request, response);
            } else {
                // Senha incorreta
                logger.log(Level.WARNING, "Tentativa de login inválida: Senha incorreta para o login: " + login);
                response.sendRedirect("");
            }
        } else {
            // Matrícula não encontrada
            logger.log(Level.WARNING, "Tentativa de login inválida: Login não encontrado: " + login);
            response.sendRedirect("");
        }
	}

}
