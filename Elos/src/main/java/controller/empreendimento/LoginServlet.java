package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.EmpreendimentoDAO;
import model.entity.Aluno;
import model.entity.Empreendimento;
import model.entity.EmpreendimentoAluno;
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
		RequestDispatcher dispatcher = request.getRequestDispatcher("/empreendimento/login.jsp");
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
			response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=campos_vazios");
			return;
		}

		try {
			if (empreendimentoDAO.verificarUsuarioExistente(login)) {
				String storedHash = empreendimentoDAO.obterSenhaPorLoginOuEmail(login);
				if (storedHash != null && PasswordUtils.checkPassword(senha, storedHash)) {
					int id = empreendimentoDAO.obterId(login);
					request.getSession().setAttribute("id", id);
					response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
				} else {
					logger.log(Level.WARNING, "Senha incorreta para login: " + login);
					response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=login_falhou");
				}
			} else {
				logger.log(Level.WARNING, "Login não encontrado: " + login);
				response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=login_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar login para: " + login, e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=erro_interno");
		}
	}

	private void processarCadastro(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String nome = request.getParameter("nome");
		String email = request.getParameter("email");
		String numeroTelefone = request.getParameter("phoneNumber");
		String cidade = request.getParameter("city");
		String tipo = request.getParameter("tipo");

		String senha = request.getParameter("senha");
		String confirmarSenha = request.getParameter("confirmarSenha");
		String login = request.getParameter("loginEmpreendimento");

		String cursoGeralId = request.getParameter("cursoGeralId");
		String anoSemestre = request.getParameter("anoSemestre");

		if (senha == null || !senha.equals(confirmarSenha) || login == null || login.trim().isEmpty()) {
			logger.log(Level.WARNING, "Senhas diferentes ou login do empreendimento vazio.");
			response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=senhas_diferentes");
			return;
		}

		String cursoENUM = null;
		if (cursoGeralId != null) {
			switch (cursoGeralId) {
			case "1":
				cursoENUM = "DESENVOLVIMENTO_SISTEMAS";
				break;
			case "2":
				cursoENUM = "ENERGIA_RENOVAVEIS";
				break;
			}
		}
		
		String tipoBD = "PADRAO";
		if ("ALUNO_SOLIDARIO".equals(tipo)) {
			tipoBD = "ALUNO";
		}

		try {
			if (empreendimentoDAO.verificarUsuarioExistente(login)) {
				response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=usuario_existente");
				return;
			}

			String senhaHash = PasswordUtils.hashPassword(senha);

			Empreendimento empreendimento = new Empreendimento(0, nome, email, senhaHash, login, tipoBD,
					numeroTelefone, cidade, null, null, null);

			boolean sucessoEmpreendimento = empreendimentoDAO.incluirEmpreendimento(empreendimento);
			int empreendimentoId = empreendimento.getId();

			if (sucessoEmpreendimento) {
				boolean sucessoAlunos = true;

				if ("ALUNO".equals(tipoBD) && empreendimentoId > 0 && cursoENUM != null && anoSemestre != null
						&& !anoSemestre.trim().isEmpty()) {

					for (String paramName : request.getParameterMap().keySet()) {
						if (paramName.startsWith("alunos[") && paramName.endsWith("].nome")) {
							String indexKey = paramName.substring(7, paramName.indexOf("]."));

							String nomeAluno = request.getParameter(paramName);
							String matriculaAluno = request.getParameter("alunos[" + indexKey + "].matricula");

							if (nomeAluno != null && matriculaAluno != null) {
								Aluno aluno = new Aluno(0, nomeAluno, matriculaAluno, cursoENUM);
								int alunoId = empreendimentoDAO.incluirAluno(aluno);

								if (alunoId > 0) {
									EmpreendimentoAluno ea = new EmpreendimentoAluno(empreendimentoId, alunoId,
											anoSemestre);
									if (!empreendimentoDAO.incluirEmpreendimentoAluno(ea)) {
										sucessoAlunos = false;
									}
								} else {
									sucessoAlunos = false;
								}
							}
						}
					}
				}
				else if ("ALUNO".equals(tipoBD) && (anoSemestre == null || anoSemestre.trim().isEmpty())) {
					sucessoAlunos = false;
					logger.log(Level.WARNING, "Empreendimento Solidário de Alunos sem Ano/Semestre preenchido.");
				}

				if (sucessoAlunos) {
					request.getSession().setAttribute("id", empreendimentoId);
					response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
				} else {
					response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=cadastro_alunos_falhou");
				}

			} else {
				response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=cadastro_falhou");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar cadastro para: " + login, e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/login.jsp?erro=erro_interno");
		}
	}
}