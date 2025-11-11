package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.ProdutoDAO;
import model.dao.VendaDAO;
import model.entity.Produto;
import model.entity.Venda;
import model.entity.VendaProduto;
import services.VendaHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/venda/listagem", "/empreendimento/venda/dashboard",
		"/empreendimento/venda/visualizar", "/empreendimento/venda/incluir", "/empreendimento/venda/editar",
		"/empreendimento/venda/excluir" })
public class VendaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(VendaServlet.class.getName());
	
	VendaDAO vendaDAO = new VendaDAO();
	ProdutoDAO produtoDAO = new ProdutoDAO();

	public VendaServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/venda/listagem":
			visualizarListagem(request, response);
			break;
		case "/empreendimento/venda/dashboard":
			visualizarDashboard(request, response);
			break;
		case "/empreendimento/venda/visualizar":
			visualizarVenda(request, response);
			break;
		case "/empreendimento/venda/incluir":
			visualizarInclusao(request, response);
			break;	
		case "/empreendimento/venda/editar":
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
		case "/empreendimento/venda/incluir":
			processarInclusao(request, response);
			break;
		case "/empreendimento/venda/editar":
			processarEdicao(request, response);
			break;
		case "/empreendimento/venda/excluir":
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
			ArrayList<Venda> vendas = vendaDAO.listarVendas(empreendimentoId);
			request.setAttribute("vendas", vendas);
			RequestDispatcher rd = request.getRequestDispatcher("listagem.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de vendas", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/venda/dashboard");
		}
	}

	private void visualizarDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			VendaHelper helper = new VendaHelper();
			Map<String, Object> dashboardData = helper.prepararDadosDashboard(empreendimentoId);
			request.setAttribute("dashboardData", dashboardData);
			
			ArrayList<Venda> vendas = vendaDAO.listarVendas(empreendimentoId);
			request.setAttribute("vendas", vendas);
			
			RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de vendas", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
		}
	}

	private void visualizarVenda(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");

		try {
			String idParam = request.getParameter("id");
			if (idParam == null || idParam.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da venda não fornecido.");
				return;
			}

			int vendaId = Integer.parseInt(idParam);
			Venda venda = vendaDAO.obterVendaPorId(vendaId, empreendimentoId);

			if (venda != null) {
				request.setAttribute("venda", venda);
				RequestDispatcher rd = request.getRequestDispatcher("visualizar.jsp");
				rd.forward(request, response);
			} else {
				logger.log(Level.SEVERE, "Venda não encontrada.");
				response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
			}
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "ID de venda inválido: " + request.getParameter("id"), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de venda inválido.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de visualização de venda", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
		}
	}

	private void visualizarInclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");

		try {
			ArrayList<Produto> produtos = produtoDAO.listarProdutos(empreendimentoId);
			request.setAttribute("produtos", produtos);
			RequestDispatcher rd = request.getRequestDispatcher("incluir.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de inclusão de vendas", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
		}
	}
	
	private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");

		try {
			String idParam = request.getParameter("id");
			if (idParam == null || idParam.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da venda não fornecido.");
				return;
			}

			int vendaId = Integer.parseInt(idParam);
			Venda venda = vendaDAO.obterVendaPorId(vendaId, empreendimentoId);

			if (venda != null) {
				ArrayList<Produto> todosProdutos = produtoDAO.listarProdutos(empreendimentoId);

				request.setAttribute("venda", venda);
				request.setAttribute("produtos", todosProdutos);

				RequestDispatcher rd = request.getRequestDispatcher("editar.jsp");
				rd.forward(request, response);
			} else {
				logger.log(Level.SEVERE, "Venda não encontrada.");
				response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "ID de venda inválido: " + request.getParameter("id"), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de venda inválido.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de vendas", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
		}
	}

	// POST //
	private void processarInclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String dataVendaStr = request.getParameter("dataVenda");

			String[] produtoIds = request.getParameterValues("produtoId");
			String[] quantidades = request.getParameterValues("quantidade");
			String[] precosUnitarios = request.getParameterValues("valorUnitario");

			if (produtoIds == null || produtoIds.length == 0) {
				throw new IllegalStateException("Nenhum produto foi adicionado à venda.");
			}
			
			List<VendaProduto> produtosDaVenda = new ArrayList<>();
			double valorTotalCalculado = 0.0;

			for (int i = 0; i < produtoIds.length; i++) {
				int produtoId = Integer.parseInt(produtoIds[i]);
				double quantidade = Double.parseDouble(quantidades[i]);
				double precoUnitario = Double.parseDouble(precosUnitarios[i]);
				
				VendaProduto produto = new VendaProduto();
				produto.setProdutoId(produtoId);
				produto.setPrecoUnitario(precoUnitario);
				produto.setQuantidade(quantidade);
				
				produtosDaVenda.add(produto);
				
				valorTotalCalculado += (quantidade * precoUnitario);
			}

			Venda novaVenda = new Venda();
			novaVenda.setEmpreendimentoId(empreendimentoId);
			novaVenda.setValorTotal(valorTotalCalculado);
			novaVenda.setDataVenda(dataVendaStr);

			vendaDAO.registrarVenda(novaVenda, produtosDaVenda);
			
			mensagem = "Venda incluída com sucesso!";

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao incluir venda: ", e);
			mensagem = "Erro ao incluir a venda.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
	}

	private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String vendaIdStr = request.getParameter("id");
			int vendaId = Integer.parseInt(vendaIdStr);
			
			String dataVendaStr = request.getParameter("dataVenda");

			String[] produtoIds = request.getParameterValues("produtoId");
			String[] quantidades = request.getParameterValues("quantidade");
			String[] precosUnitarios = request.getParameterValues("valorUnitario");

			if (produtoIds == null || produtoIds.length == 0) {
				throw new IllegalStateException("Nenhum produto foi adicionado à venda.");
			}
			
			List<VendaProduto> produtosDaVenda = new ArrayList<>();
			double valorTotalCalculado = 0.0;

			for (int i = 0; i < produtoIds.length; i++) {
				int produtoId = Integer.parseInt(produtoIds[i]);
				double quantidade = Double.parseDouble(quantidades[i]);
				double precoUnitario = Double.parseDouble(precosUnitarios[i]);
				
				VendaProduto produto = new VendaProduto();
				produto.setProdutoId(produtoId);
				produto.setPrecoUnitario(precoUnitario);
				produto.setQuantidade(quantidade);
				
				produtosDaVenda.add(produto);
				
				valorTotalCalculado += (quantidade * precoUnitario);
			}

			Venda venda = new Venda();
			venda.setId(vendaId);
			venda.setEmpreendimentoId(empreendimentoId);
			venda.setValorTotal(valorTotalCalculado);
			venda.setDataVenda(dataVendaStr);

			vendaDAO.editarVenda(venda, produtosDaVenda);
			mensagem = "Venda editada com sucesso!";

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao editar venda: ", e);
			mensagem = "Erro ao editar a venda.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
	}
	
	private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String vendaIdStr = request.getParameter("id");
			int vendaId = (vendaIdStr == null || vendaIdStr.isEmpty()) ? 0 : Integer.parseInt(vendaIdStr);

			if (vendaId == 0) {
				mensagem = "Venda não encontrada para exclusão.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
				return;
			}

			boolean exclusao = vendaDAO.excluirVenda(vendaId, empreendimentoId);
			if (exclusao) {
				mensagem = "Venda excluída com sucesso.";
			} else {
				mensagem = "Não foi possível excluir a venda.";
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID da venda para exclusão: " + request.getParameter("id"), e);
			mensagem = "Falha ao excluir venda.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao excluir venda", e);
			mensagem = "Falha ao excluir venda.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/venda/listagem");
	}

}