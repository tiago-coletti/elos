package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.CompraInsumoDAO;
import model.dao.InsumoDAO;
import model.entity.CompraInsumo;
import model.entity.Insumo;
import services.InsumoHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/insumo/listagem", "/empreendimento/insumo/dashboard",
		"/empreendimento/insumo/visualizar", "/empreendimento/insumo/incluir", "/empreendimento/insumo/editar",
		"/empreendimento/insumo/excluir", "/empreendimento/insumo/ajustar-estoque" })
public class InsumoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InsumoServlet.class.getName());

	private final InsumoDAO insumoDAO = new InsumoDAO();
	private final CompraInsumoDAO compraInsumoDAO = new CompraInsumoDAO();
	
	public InsumoServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/insumo/listagem":
			visualizarListagem(request, response);
			break;
		case "/empreendimento/insumo/dashboard":
			visualizarDashboard(request, response);
			break;
		case "/empreendimento/insumo/visualizar":
			visualizarInsumo(request, response);
			break;
		case "/empreendimento/insumo/editar":
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
		case "/empreendimento/insumo/incluir":
			processarInclusao(request, response);
			break;
		case "/empreendimento/insumo/editar":
			processarEdicao(request, response);
			break;
		case "/empreendimento/insumo/excluir":
			processarExclusao(request, response);
			break;
		case "/empreendimento/insumo/ajustar-estoque":
			processarAjusteEstoque(request, response);
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
			ArrayList<Insumo> insumos = insumoDAO.listarInsumos(empreendimentoId);
			request.setAttribute("insumos", insumos);
			RequestDispatcher rd = request.getRequestDispatcher("listagem.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de insumos", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/dashboard");
		}
	}

	private void visualizarDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			InsumoHelper helper = new InsumoHelper();
			Map<String, Object> dashboardData = helper.prepararDadosDashboard(empreendimentoId);
			request.setAttribute("dashboardData", dashboardData);
			
			ArrayList<Insumo> insumos = insumoDAO.listarInsumos(empreendimentoId);
			request.setAttribute("insumos", insumos);
			
			RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de insumos", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
		}
	}

	private void visualizarInsumo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");

		try {
			String idParam = request.getParameter("id");
			if (idParam == null || idParam.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do insumo não fornecido.");
				return;
			}

			int insumoId = Integer.parseInt(idParam);
			Insumo insumo = insumoDAO.obterInsumoPorId(insumoId, empreendimentoId);

			if (insumo == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Insumo não encontrado.");
				return;
			}

			ArrayList<CompraInsumo> lotes = compraInsumoDAO.listarLotesPorInsumoId(insumoId, empreendimentoId);
			
			double totalEstoque = 0;
			for(CompraInsumo lote : lotes) {
				totalEstoque += lote.getQuantidadeRestante();
			}

			request.setAttribute("insumo", insumo);
			request.setAttribute("lotes", lotes);
			request.setAttribute("totalEstoque", totalEstoque);

			RequestDispatcher rd = request.getRequestDispatcher("visualizar.jsp");
			rd.forward(request, response);

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "ID de insumo inválido: " + request.getParameter("id"), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de insumo inválido.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de visualização de insumo", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
		}
	}

	private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        String idParam = request.getParameter("id");
	        if (idParam == null || idParam.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do insumo não fornecido.");
	            return;
	        }

	        int insumoId = Integer.parseInt(idParam);
	        Insumo insumo = insumoDAO.obterInsumoPorId(insumoId, empreendimentoId);

	        if (insumo != null) {
	            request.setAttribute("insumo", insumo);
	            RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
	            rd.forward(request, response);
	        } else {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Insumo não encontrado.");
	        }
	    } catch (NumberFormatException e) {
	        logger.log(Level.WARNING, "ID de insumo inválido: " + request.getParameter("id"), e);
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de insumo inválido.");
	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de insumos", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
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
			String unidadeMedida = request.getParameter("unidadeMedida");

			Insumo insumo = new Insumo(0, nome, unidadeMedida, 0, empreendimentoId);
			insumoDAO.incluirInsumo(insumo);
			mensagem = "Insumo incluído com sucesso!";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao incluir insumo: ", e);
			mensagem = "Erro ao incluir o insumo.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
	}

	private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String insumoIdStr = request.getParameter("id");
			int insumoId = (insumoIdStr == null || insumoIdStr.isEmpty()) ? 0 : Integer.parseInt(insumoIdStr);

			if (insumoId == 0) {
				mensagem = "Insumo não encontrado para edição.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
				return;
			}

			Insumo insumoExistente = insumoDAO.obterInsumoPorId(insumoId, empreendimentoId);
			if (insumoExistente == null) {
				mensagem = "Insumo não encontrado para edição.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
				return;
			}

			String nome = request.getParameter("nome");
			String unidadeMedida = request.getParameter("unidadeMedida");

			Insumo insumo = new Insumo(insumoId, nome, unidadeMedida, 0, empreendimentoId);
			insumoDAO.editarInsumo(insumo);
			mensagem = "Insumo editado com sucesso!";

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID durante edição: " + request.getParameter("id") + ", ", e);
			mensagem = "Falha ao editar insumo.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao editar insumo: ", e);
			mensagem = "Falha ao editar insumo.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
	}

	private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String insumoIdStr = request.getParameter("id");
			int insumoId = (insumoIdStr == null || insumoIdStr.isEmpty()) ? 0 : Integer.parseInt(insumoIdStr);

			if (insumoId == 0) {
				mensagem = "Insumo não encontrado para exclusão.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
				return;
			}

			boolean exclusao = insumoDAO.excluirInsumo(insumoId, empreendimentoId);
			if (exclusao) {
				mensagem = "Insumo excluído com sucesso.";
			} else {
				mensagem = "Não foi possível excluir o insumo.";
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID do insumo para exclusão: " + request.getParameter("id"), e);
			mensagem = "Falha ao excluir insumo.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao excluir insumo", e);
			mensagem = "Falha ao excluir insumo.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/listagem");
	}
	
	private void processarAjusteEstoque(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		// Já pegamos o ID do insumo aqui, pois ele é usado tanto na lógica quanto no redirect
		String insumoIdRedirect = request.getParameter("insumoId"); 
		String mensagem;

		try {
			// --- Validação dos IDs ---
			if (insumoIdRedirect == null || insumoIdRedirect.isEmpty()) {
				throw new Exception("ID do insumo principal não fornecido.");
			}
			int insumoId = Integer.parseInt(insumoIdRedirect);
			int compraInsumoId = Integer.parseInt(request.getParameter("compraInsumoId"));
			
			// --- Dados do Formulário ---
			double quantidade = Double.parseDouble(request.getParameter("quantidade"));
			String tipoAjuste = request.getParameter("tipoAjuste");
			
			if (quantidade <= 0) {
				throw new Exception("Quantidade deve ser maior que zero.");
			}

			double deltaQuantidade;
			switch (tipoAjuste) {
				case "perda":
				case "retirada_avulsa": 
					deltaQuantidade = -quantidade;
					break;
				case "entrada_avulsa": 
					deltaQuantidade = quantidade;
					break;
				default:
					deltaQuantidade = 0; 
			}

			double custoUnitario = compraInsumoDAO.obterCustoLotePorId(compraInsumoId, empreendimentoId);
			if (custoUnitario < 0) {
				throw new Exception("Lote de insumo não encontrado ou não pertence ao empreendimento.");
			}

			boolean sucessoLote = compraInsumoDAO.ajustarEstoque(compraInsumoId, empreendimentoId, quantidade, tipoAjuste, custoUnitario);
			
			if (sucessoLote) {
				if (deltaQuantidade != 0) {
					insumoDAO.ajustarEstoqueTotal(insumoId, empreendimentoId, deltaQuantidade);
				}
				mensagem = "Estoque ajustado com sucesso!";
			} else {
				throw new Exception("Falha ao ajustar estoque do lote, verifique o valor informado.");
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "Parâmetros inválidos para ajuste de estoque.", e);
			mensagem = "Erro: Dados inválidos.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao processar ajuste de estoque.", e);
			mensagem = e.getMessage() != null ? e.getMessage() : "Erro desconhecido ao ajustar estoque.";
		}
		
		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/insumo/visualizar?id=" + insumoIdRedirect);
	}

}