package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.MaoObraDAO;
import model.entity.MaoObra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/mao-obra/listagem", "/empreendimento/mao-obra/dashboard",
		"/empreendimento/mao-obra/visualizar", "/empreendimento/mao-obra/incluir", "/empreendimento/mao-obra/editar",
		"/empreendimento/mao-obra/excluir" })
public class MaoObraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MaoObraServlet.class.getName());
	
	private final MaoObraDAO maoObraDAO = new MaoObraDAO();

    public MaoObraServlet() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/mao-obra/listagem":
			visualizarListagem(request, response);
			break;
		case "/empreendimento/mao-obra/dashboard":
			visualizarDashboard(request, response);
			break;
		case "/empreendimento/mao-obra/visualizar":
			visualizarMaoObra(request, response);
			break;
		case "/empreendimento/mao-obra/editar":
			visualizarEdicao(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rota não reconhecida.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/mao-obra/incluir":
			processarInclusao(request, response);
			break;
		case "/empreendimento/mao-obra/editar":
			processarEdicao(request, response);
			break;
		case "/empreendimento/mao-obra/excluir":
			processarExclusao(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rota não reconhecida.");
		}
	}

	// GET //

	private void visualizarListagem(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			ArrayList<MaoObra> maosObra = maoObraDAO.listarMaosObra(empreendimentoId);
			request.setAttribute("maosObra", maosObra);
			RequestDispatcher rd = request.getRequestDispatcher("listagem.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de listagem de mãos de obra", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/dashboard");
		}
	}

	private void visualizarDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			ArrayList<MaoObra> maosObra = maoObraDAO.listarMaosObra(empreendimentoId);
			request.setAttribute("maosObra", maosObra);
			RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para o dashboard de mãos de obra", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
		}
	}

	private void visualizarMaoObra(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        String idParam = request.getParameter("id");
	        if (idParam == null || idParam.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da mão de obra não fornecido.");
	            return;
	        }

	        int maoObraId = Integer.parseInt(idParam);
	        MaoObra maoObra = maoObraDAO.obterMaoObraPorId(maoObraId, empreendimentoId);

	        if (maoObra != null) {
	            request.setAttribute("maoObra", maoObra);
	            RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
	            rd.forward(request, response);
	        } else {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Mão de obra não encontrada.");
	        }
	    } catch (NumberFormatException e) {
	        logger.log(Level.WARNING, "ID de mão de obra inválido: " + request.getParameter("id"), e);
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de mão de obra inválido.");
	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de mão de obra", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/maoobra/listagem");
	    }
	}

	// POST //

	private void processarInclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String nome = request.getParameter("nome");
			String custoHoraStr = request.getParameter("custoHora");
			
			double custoHora = 0.0;
			try {
				custoHora = Double.parseDouble(custoHoraStr);
			} catch (NumberFormatException nfe) {
		        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de mão de obra", nfe);
		        response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
			}

			MaoObra maoObra = new MaoObra(0, nome, custoHora, empreendimentoId);
			maoObraDAO.incluirMaoObra(maoObra);
			mensagem = "Mão de obra incluída com sucesso!";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao incluir mão de obra: ", e);
			mensagem = "Erro ao incluir o mão de obra.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
	}

	private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String maoObraIdStr = request.getParameter("id");
			int maoObraId = (maoObraIdStr == null || maoObraIdStr.isEmpty()) ? 0 : Integer.parseInt(maoObraIdStr);

			if (maoObraId == 0) {
				mensagem = "Mão de obra não encontrada para edição.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
				return;
			}

			MaoObra maoObraExistente = maoObraDAO.obterMaoObraPorId(maoObraId, empreendimentoId);
			if (maoObraExistente == null) {
				mensagem = "Mão de obra não encontrada para edição.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
				return;
			}

			String nome = request.getParameter("nome");
			String custoHoraStr = request.getParameter("custoHora");

			double custoHora = 0.0;
			try {
				custoHora = Double.parseDouble(custoHoraStr);
			} catch (NumberFormatException nfe) {
		        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de mão de obra", nfe);
		        response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
			}

			MaoObra maoObra = new MaoObra(maoObraId, nome, custoHora, empreendimentoId);
			maoObraDAO.editarMaoObra(maoObra);
			mensagem = "Mão de obra editada com sucesso!";

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID durante edição: " + request.getParameter("id") + ", ", e);
			mensagem = "Falha ao editar mão de obra.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao editar mão de obra: ", e);
			mensagem = "Falha ao editar mão de obra.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
	}

	private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String maoObraIdStr = request.getParameter("id");
			int maoObraId = (maoObraIdStr == null || maoObraIdStr.isEmpty()) ? 0 : Integer.parseInt(maoObraIdStr);

			if (maoObraId == 0) {
				mensagem = "Mão de obra não encontrada para exclusão.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
				return;
			}

			boolean exclusao = maoObraDAO.excluirMaoObra(maoObraId, empreendimentoId);
			if (exclusao) {
				mensagem = "Mão de obra excluída com sucesso.";
			} else {
				mensagem = "Não foi possível excluir a mão de obra.";
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID da mão de obra para exclusão: " + request.getParameter("id"), e);
			mensagem = "Falha ao excluir mão de obra.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao excluir mão de obra", e);
			mensagem = "Falha ao excluir mão de obra.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
	}

}
