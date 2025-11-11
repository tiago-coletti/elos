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
import services.MaoObraHelper; // Importado

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map; // Importado
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/mao-obra/listagem", "/empreendimento/mao-obra/dashboard",
		"/empreendimento/mao-obra/visualizar", "/empreendimento/mao-obra/incluir", "/empreendimento/mao-obra/editar",
		"/empreendimento/mao-obra/excluir" })
public class MaoObraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MaoObraServlet.class.getName());
	
	private final MaoObraDAO maoObraDAO = new MaoObraDAO();
	private final MaoObraHelper maoObraHelper = new MaoObraHelper(); // Instanciado

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
			// Usa o Helper para buscar dados agregados
			Map<String, Object> dashboardData = maoObraHelper.prepararDadosDashboard(empreendimentoId);
			request.setAttribute("dashboardData", dashboardData);
			
			// Busca a lista para a tabela (mantido)
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
		// Implementação futura, se necessário
		response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
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
	        response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem"); // Corrigido o redirect
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
				// Substitui vírgula por ponto para o parse funcionar corretamente
				custoHora = Double.parseDouble(custoHoraStr.replace(",", "."));
			} catch (NumberFormatException | NullPointerException nfe) {
		        logger.log(Level.WARNING, "Custo por hora inválido: " + custoHoraStr, nfe);
		        mensagem = "Erro: Custo por hora inválido.";
		        session.setAttribute("mensagem", mensagem);
		        response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/listagem");
		        return; // Interrompe a execução
			}

			MaoObra maoObra = new MaoObra(0, nome, custoHora, empreendimentoId);
			maoObraDAO.incluirMaoObra(maoObra);
			mensagem = "Mão de obra incluída com sucesso!";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao incluir mão de obra: ", e);
			mensagem = "Erro ao incluir a mão de obra.";
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
				// Substitui vírgula por ponto para o parse
				custoHora = Double.parseDouble(custoHoraStr.replace(",", "."));
			} catch (NumberFormatException | NullPointerException nfe) {
		        logger.log(Level.WARNING, "Custo por hora inválido na edição: " + custoHoraStr, nfe);
		        mensagem = "Erro: Custo por hora inválido.";
		        session.setAttribute("mensagem", mensagem);
		        // Redireciona de volta para a edição com o ID
		        response.sendRedirect(request.getContextPath() + "/empreendimento/mao-obra/editar?id=" + maoObraId); 
		        return; // Interrompe
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
				// Adiciona verificação se está associada a produto
				// (Implementar método no DAO se necessário)
				// if (maoObraDAO.estaAssociadaAProduto(maoObraId)) {
				// mensagem = "Não é possível excluir: Mão de obra associada a um ou mais produtos.";
				// } else {
					mensagem = "Não foi possível excluir a mão de obra.";
				// }
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