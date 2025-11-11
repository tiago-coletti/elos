package controller.admin; 

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.AdminDAO; 
import model.entity.Admin; 
import util.PasswordUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/admin/login", "/admin/cadastro" }) 
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String CODIGO_MESTRE = "ELOS_ADMIN_2025";
	
	private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
	AdminDAO adminDAO = new AdminDAO(); 

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/login.html");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();

		switch (path) {
		case "/admin/login": 
			processarLogin(request, response);
			break;
		case "/admin/cadastro": 
			processarCadastro(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requisição inválida.");
		}
	}

	private void processarLogin(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String login = request.getParameter("login");
		String senha = request.getParameter("senha");

		if (login == null || login.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
			logger.log(Level.WARNING, "Login ou senha de admin estão vazios.");
			response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=campos_vazios");
			return;
		}

		try {
			if (adminDAO.verificarUsuarioExistente(login)) {
				String storedHash = adminDAO.obterSenhaPorLoginOuEmail(login);
				if (storedHash != null && PasswordUtils.checkPassword(senha, storedHash)) {
					
					long id = adminDAO.obterId(login); 
					
					request.getSession().setAttribute("adminId", id);
					
					response.sendRedirect(request.getContextPath() + "/admin/dashboard");
				} else {
					logger.log(Level.WARNING, "Senha incorreta para admin login: " + login);
					response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=login_falhou");
				}
			} else {
				logger.log(Level.WARNING, "Admin login não encontrado: " + login);
				response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=login_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar login de admin para: " + login, e);
			response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=erro_interno");
		}
	}

	private void processarCadastro(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// 1. Coleta de Parâmetros do Admin
		String nome = request.getParameter("nome");
		String email = request.getParameter("email");
		String senha = request.getParameter("senha");
		String confirmarSenha = request.getParameter("confirmarSenha");
		String login = request.getParameter("loginAdmin");
		String codigoVerificacao = request.getParameter("codigoVerificacao"); 

		// 2. Validação do Código de Verificação (REQUISITO CHAVE)
		if (codigoVerificacao == null || !codigoVerificacao.equals(CODIGO_MESTRE)) {
			logger.log(Level.WARNING, "Tentativa de cadastro de admin com código de verificação inválido.");
			response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=codigo_invalido");
			return;
		}

		// 3. Validação Básica
		if (senha == null || !senha.equals(confirmarSenha) || login == null || login.trim().isEmpty()) {
			logger.log(Level.WARNING, "Senhas diferentes ou login do admin vazio.");
			response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=senhas_diferentes");
			return;
		}

		try {
			// 4. Verificação de Existência
			if (adminDAO.verificarUsuarioExistente(login)) {
				response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=usuario_existente");
				return;
			}

			// 5. Criação e Hash da Senha
			String senhaHash = PasswordUtils.hashPassword(senha);

			// 6. Criação da Entidade Admin (usando o POJO)
			Admin admin = new Admin(0, nome, login, email, senhaHash);

			// 7. Inclusão no Banco de Dados
			boolean sucessoAdmin = adminDAO.incluirAdmin(admin);
			long adminId = admin.getId();

			if (sucessoAdmin) {
				// 8. Resposta Final
				request.getSession().setAttribute("adminId", adminId);
				response.sendRedirect(request.getContextPath() + "/admin/dashboard"); // Vai para o dashboard de admin

			} else {
				response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=cadastro_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar cadastro de admin para: " + login, e);
			response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=erro_interno");
		}
	}
}