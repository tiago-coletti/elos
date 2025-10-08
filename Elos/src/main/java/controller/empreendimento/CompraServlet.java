package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.CompraDAO;
import model.dao.InsumoDAO;
import model.entity.Compra;
import model.entity.CompraInsumo;
import model.entity.Insumo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/compra/listagem", "/empreendimento/compra/dashboard",
		"/empreendimento/compra/visualizar", "/empreendimento/compra/incluir", "/empreendimento/compra/editar",
		"/empreendimento/compra/excluir" })
public class CompraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CompraServlet.class.getName());
       
	CompraDAO compraDAO = new CompraDAO();
	InsumoDAO insumoDAO = new InsumoDAO();

    public CompraServlet() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/compra/listagem":
			visualizarListagem(request, response);
			break;
		case "/empreendimento/compra/dashboard":
			visualizarDashboard(request, response);
			break;
		case "/empreendimento/compra/visualizar":
			visualizarCompra(request, response);
			break;
		case "/empreendimento/compra/incluir":
			visualizarInclusao(request, response);
			break;	
		case "/empreendimento/compra/editar":
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
		case "/empreendimento/compra/incluir":
			processarInclusao(request, response);
			break;
		case "/empreendimento/compra/editar":
			processarEdicao(request, response);
			break;
		case "/empreendimento/compra/excluir":
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
			ArrayList<Compra> compras = compraDAO.listarCompras(empreendimentoId);
			request.setAttribute("compras", compras);
			RequestDispatcher rd = request.getRequestDispatcher("listagem.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de compras", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/compra/dashboard");
		}
	}

	private void visualizarDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			ArrayList<Compra> compras = compraDAO.listarCompras(empreendimentoId);
			request.setAttribute("compras", compras);
			RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de compras", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
		}
	}

	private void visualizarCompra(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	private void visualizarInclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        ArrayList<Insumo> insumos = insumoDAO.listarInsumos(empreendimentoId);
	        request.setAttribute("insumos", insumos);
			RequestDispatcher rd = request.getRequestDispatcher("incluir.jsp");
			rd.forward(request, response);
	    }  catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de inclusão de compras", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	    }
	}
	
	private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        String idParam = request.getParameter("id");
	        if (idParam == null || idParam.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da compra não fornecido.");
	            return;
	        }

	        int compraId = Integer.parseInt(idParam);
	        Compra compra = compraDAO.obterCompraPorId(compraId, empreendimentoId);

	        if (compra != null) {
	            ArrayList<Insumo> todosInsumos = insumoDAO.listarInsumos(empreendimentoId);

	            request.setAttribute("compra", compra); 
	            request.setAttribute("insumos", todosInsumos);

	            RequestDispatcher rd = request.getRequestDispatcher("editar.jsp"); 
	            rd.forward(request, response);
	        } else {
		        logger.log(Level.SEVERE, "Compra não encontrada.");
		        response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	        }

	    } catch (NumberFormatException e) {
	        logger.log(Level.WARNING, "ID de compra inválido: " + request.getParameter("id"), e);
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de compra inválido.");
	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de compras", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	    }
	}

	// POST //
	private void processarInclusao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");
	    String mensagem;

	    try {
	        String dataCompraStr = request.getParameter("dataCompra");

	        String[] insumoIds = request.getParameterValues("insumoId");
	        String[] quantidades = request.getParameterValues("quantidade");
	        String[] precosUnitarios = request.getParameterValues("valorUnitario");

	        if (insumoIds == null || insumoIds.length == 0) {
	            throw new IllegalStateException("Nenhum insumo foi adicionado à compra.");
	        }
	        
	        List<CompraInsumo> insumosDaCompra = new ArrayList<>();
	        double valorTotalCalculado = 0.0;

	        for (int i = 0; i < insumoIds.length; i++) {
	            int insumoId = Integer.parseInt(insumoIds[i]);
	            double quantidade = Double.parseDouble(quantidades[i]);
	            double precoUnitario = Double.parseDouble(precosUnitarios[i]);
	            
	            CompraInsumo insumo = new CompraInsumo();
	            insumo.setInsumoId(insumoId);
	            insumo.setPrecoUnitario(precoUnitario);
	            insumo.setQuantidadeComprada(quantidade);
	            insumo.setQuantidadeRestante(quantidade);
	            
	            insumosDaCompra.add(insumo);
	            
	            valorTotalCalculado += (quantidade * precoUnitario);
	        }

	        Compra novaCompra = new Compra();
	        novaCompra.setEmpreendimentoId(empreendimentoId);
	        novaCompra.setValorTotal(valorTotalCalculado);
	        novaCompra.setDataCompra(dataCompraStr);

	        CompraDAO compraDAO = new CompraDAO();
	        compraDAO.registrarCompra(novaCompra, insumosDaCompra); 
	        
	        mensagem = "Compra incluída com sucesso!";

	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao incluir compra: ", e);
	        mensagem = "Erro ao incluir a compra.";
	    }

	    session.setAttribute("mensagem", mensagem);
	    response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	}

	private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");
	    String mensagem;

	    try {
	        String compraIdStr = request.getParameter("id");
	        int compraId = Integer.parseInt(compraIdStr);
	        
	        String dataCompraStr = request.getParameter("dataCompra");

	        String[] insumoIds = request.getParameterValues("insumoId");
	        String[] quantidades = request.getParameterValues("quantidade");
	        String[] precosUnitarios = request.getParameterValues("valorUnitario");

	        if (insumoIds == null || insumoIds.length == 0) {
	            throw new IllegalStateException("Nenhum insumo foi adicionado à compra.");
	        }
	        
	        List<CompraInsumo> insumosDaCompra = new ArrayList<>();
	        double valorTotalCalculado = 0.0;

	        for (int i = 0; i < insumoIds.length; i++) {
	            int insumoId = Integer.parseInt(insumoIds[i]);
	            double quantidade = Double.parseDouble(quantidades[i]);
	            double precoUnitario = Double.parseDouble(precosUnitarios[i]);
	            
	            CompraInsumo insumo = new CompraInsumo();
	            insumo.setInsumoId(insumoId);
	            insumo.setPrecoUnitario(precoUnitario);
	            insumo.setQuantidadeComprada(quantidade);
	            insumo.setQuantidadeRestante(quantidade);
	            
	            insumosDaCompra.add(insumo);
	            
	            valorTotalCalculado += (quantidade * precoUnitario);
	        }

	        Compra compra = new Compra();
	        compra.setId(compraId);
	        compra.setEmpreendimentoId(empreendimentoId);
	        compra.setValorTotal(valorTotalCalculado);
	        compra.setDataCompra(dataCompraStr); 

	        compraDAO.editarCompra(compra, insumosDaCompra);
	        mensagem = "Compra editada com sucesso!";

	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao editar compra: ", e);
	        mensagem = "Erro ao editar a compra.";
	    }

	    session.setAttribute("mensagem", mensagem);
	    response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	}
	
	private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String compraIdStr = request.getParameter("id");
			int compraId = (compraIdStr == null || compraIdStr.isEmpty()) ? 0 : Integer.parseInt(compraIdStr);

			if (compraId == 0) {
				mensagem = "Compra não encontrada para exclusão.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
				return;
			}

			boolean exclusao = compraDAO.excluirCompra(compraId, empreendimentoId);
			if (exclusao) {
				mensagem = "Compra excluída com sucesso.";
			} else {
				mensagem = "Não foi possível excluir o compra.";
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID da compra para exclusão: " + request.getParameter("id"), e);
			mensagem = "Falha ao excluir compra.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao excluir compra", e);
			mensagem = "Falha ao excluir compra.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/compra/listagem");
	}

}
