package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.EmpreendimentoDAO;
import model.entity.Empreendimento;
import util.PasswordUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/login", "/empreendimento/cadastro" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
	EmpreendimentoDAO empreendimentoDAO = new EmpreendimentoDAO();

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/empreendimento/login.html");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();

		switch (path) {
		case "/empreendimento/login":
			processarLogin(request, response);
			break;
		case "/empreendimento/cadastro":
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
			logger.log(Level.WARNING, "Login ou senha estão vazios.");
			response.sendRedirect("/empreendimento/login.html?erro=campos_vazios");
			return;
		}

		try {
			if (empreendimentoDAO.verificarLogin(login)) {
				String storedHash = empreendimentoDAO.obterSenhaPorLogin(login);
				if (storedHash != null && PasswordUtils.checkPassword(senha, storedHash)) {
					int id = empreendimentoDAO.obterId(login);
					request.getSession().setAttribute("id", id);
					response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
				} else {
					logger.log(Level.WARNING, "Senha incorreta para login: " + login);
					response.sendRedirect(request.getContextPath() + "/empreendimento/login.html?erro=login_falhou");
				}
			} else {
				logger.log(Level.WARNING, "Login não encontrado: " + login);
				response.sendRedirect(request.getContextPath() + "/empreendimento/login.html?erro=login_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar login para: " + login, e);
		}
	}

	private void processarCadastro(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String nome = request.getParameter("nome");
		String email = request.getParameter("email");
		String senha = request.getParameter("senha");
		String confirmarSenha = request.getParameter("confirmarSenha");

		if (senha == null || !senha.equals(confirmarSenha)) {
			response.sendRedirect("/empreendimento/login.html?erro=senhas_diferentes");
			return;
		}

		try {
			if (empreendimentoDAO.verificarLogin(email)) {
				response.sendRedirect("/empreendimento/login.html?erro=usuario_existente");
				return;
			}

			senha = PasswordUtils.hashPassword(senha);
			Empreendimento empreendimento = new Empreendimento(0, nome, email, senha, null, null, null);
			boolean sucesso = empreendimentoDAO.incluirEmpreendimento(empreendimento);

			if (sucesso) {
				int id = empreendimentoDAO.obterId(email);
				request.getSession().setAttribute("id", id);
				response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
			} else {
				response.sendRedirect("/empreendimento/login.html?erro=cadastro_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar cadastro para: " + email, e);
		}
	}
}
