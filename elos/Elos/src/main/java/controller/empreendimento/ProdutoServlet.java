package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.InsumoDAO;
import model.dao.MaoObraDAO;
import model.dao.ProdutoDAO;
import model.entity.Insumo;
import model.entity.MaoObra;
import model.entity.Produto;
import model.entity.ProdutoInsumo;
import model.entity.ProdutoMaoObra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/produto/listagem", "/empreendimento/produto/dashboard",
		"/empreendimento/produto/visualizar", "/empreendimento/produto/incluir", "/empreendimento/produto/editar",
		"/empreendimento/produto/excluir" })
public class ProdutoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ProdutoServlet.class.getName());
	
	ProdutoDAO produtoDAO = new ProdutoDAO();
	InsumoDAO insumoDAO = new InsumoDAO();
	MaoObraDAO maoObraDAO = new MaoObraDAO();

    public ProdutoServlet() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		switch (path) {
		case "/empreendimento/produto/listagem":
			visualizarListagem(request, response);
			break;
		case "/empreendimento/produto/dashboard":
			visualizarDashboard(request, response);
			break;
		case "/empreendimento/produto/visualizar":
			visualizarProduto(request, response);
			break;
		case "/empreendimento/produto/incluir":
			visualizarInclusao(request, response);
			break;	
		case "/empreendimento/produto/editar":
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
		case "/empreendimento/produto/incluir":
			processarInclusao(request, response);
			break;
		case "/empreendimento/produto/editar":
			processarEdicao(request, response);
			break;
		case "/empreendimento/produto/excluir":
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
			ArrayList<Produto> produtos = produtoDAO.listarProdutos(empreendimentoId);
			request.setAttribute("produtos", produtos);
			RequestDispatcher rd = request.getRequestDispatcher("listagem.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de produtos", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/produto/dashboard");
		}
	}

	private void visualizarDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		
		try {
			ArrayList<Produto> Produtos = produtoDAO.listarProdutos(empreendimentoId);
			request.setAttribute("Produtos", Produtos);
			RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao carregar dados para a página de produtos", e);
			response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
		}
	}

	private void visualizarProduto(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	private void visualizarInclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        ArrayList<Insumo> insumos = insumoDAO.listarInsumos(empreendimentoId);
	        request.setAttribute("insumos", insumos);
	        
	        ArrayList<MaoObra> maosObra = maoObraDAO.listarMaosObra(empreendimentoId);
	        request.setAttribute("maosObra", maosObra);
	        
			RequestDispatcher rd = request.getRequestDispatcher("incluir.jsp");
			rd.forward(request, response);
	    }  catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de inclusão de produtos", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	    }
	}
	
	private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");

	    try {
	        String idParam = request.getParameter("id");
	        if (idParam == null || idParam.isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto não fornecido.");
	            return;
	        }

	        int produtoId = Integer.parseInt(idParam);
	        Produto produto = produtoDAO.obterProdutoPorId(produtoId, empreendimentoId);

	        if (produto != null) {
	            ArrayList<Insumo> todosInsumos = insumoDAO.listarInsumos(empreendimentoId);

	            request.setAttribute("produto", produto); 
	            request.setAttribute("insumos", todosInsumos);

	            RequestDispatcher rd = request.getRequestDispatcher("editar.jsp"); 
	            rd.forward(request, response);
	        } else {
		        logger.log(Level.SEVERE, "Produto não encontrada.");
		        response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	        }

	    } catch (NumberFormatException e) {
	        logger.log(Level.WARNING, "ID do produto inválido: " + request.getParameter("id"), e);
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto inválido.");
	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao carregar dados para a página de edição de produtos", e);
	        response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	    }
	}

	// POST //
	private void processarInclusao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");
	    String mensagem;

	    try {
	        String nome = request.getParameter("nomeProduto");
	        String precoVendaStr = request.getParameter("precoVenda");
	        double precoVenda = (precoVendaStr == null || precoVendaStr.isEmpty()) ? 0 : Double.parseDouble(precoVendaStr);

	        String[] insumoIds = request.getParameterValues("insumoId");
	        String[] insumoQuantidades = request.getParameterValues("insumoQuantidade"); 
	        
	        String[] maoObraIds = request.getParameterValues("maoObraId");
	        String[] maoObraHoras = request.getParameterValues("maoObraHoras");

	        List<ProdutoInsumo> produtoInsumos = new ArrayList<>();
	        if (insumoIds != null) {
	            for (int i = 0; i < insumoIds.length; i++) {
	                int insumoId = Integer.parseInt(insumoIds[i]);
	                double quantidadeUtilizada = Double.parseDouble(insumoQuantidades[i]);
	                
	                ProdutoInsumo produtoInsumo = new ProdutoInsumo();
	                produtoInsumo.setInsumoId(insumoId);
	                produtoInsumo.setQuantidadeUtilizada(quantidadeUtilizada);
	                
	                produtoInsumos.add(produtoInsumo);
	            }
	        }
	        
	        List<ProdutoMaoObra> produtoMaosObra = new ArrayList<>();
	        if (maoObraIds != null) {
	            for (int i = 0; i < maoObraIds.length; i++) {
	                int maoObraId = Integer.parseInt(maoObraIds[i]);
	                double horasUtilizadas = Double.parseDouble(maoObraHoras[i]);
	                
	                ProdutoMaoObra produtoMaoObra = new ProdutoMaoObra(); 
	                produtoMaoObra.setMaoObraId(maoObraId);
	                produtoMaoObra.setHorasUtilizadas(horasUtilizadas); 
	                
	                produtoMaosObra.add(produtoMaoObra); 
	            }
	        }

	        Produto novoProduto = new Produto();
	        novoProduto.setEmpreendimentoId(empreendimentoId);
	        novoProduto.setNome(nome);
	        novoProduto.setPrecoVenda(precoVenda);
	        
	        produtoDAO.incluirProduto(novoProduto, produtoInsumos, produtoMaosObra); 
	        
	        mensagem = "Produto incluído com sucesso!";

	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao incluir produto: ", e);
	        mensagem = "Erro ao incluir o produto.";
	    }

	    session.setAttribute("mensagem", mensagem);
	    response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	}

	private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    int empreendimentoId = (Integer) session.getAttribute("id");
	    String mensagem;

	    try {
	        String produtoIdStr = request.getParameter("id");
	        int produtoId = (produtoIdStr == null || produtoIdStr.isEmpty()) ? 0 : Integer.parseInt(produtoIdStr);
	        
	        String nome = request.getParameter("nome");
	        String precoVendaStr = request.getParameter("precoVenda");
	        double precoVenda = (precoVendaStr == null || precoVendaStr.isEmpty()) ? 0 : Double.parseDouble(precoVendaStr);

	        String[] insumoIds = request.getParameterValues("insumoId");
	        String[] maoObraIds = request.getParameterValues("maoObraId");
	        String[] quantidadesInsumosUtilizadas = request.getParameterValues("quantidadesInsumosUtilizadas");
	        String[] quantidadesMaoObraUtilizadas = request.getParameterValues("quantidadesMaosObraUtilizadas");

	        if (insumoIds == null || insumoIds.length == 0) {
	            throw new IllegalStateException("Nenhum insumo foi adicionado ao produto.");
	        } if (maoObraIds == null || maoObraIds.length == 0) {
	            throw new IllegalStateException("Nenhuma mão de obra foi adicionada ao produto.");
	        }
	        
	        List<ProdutoInsumo> produtoInsumos = new ArrayList<>();
	        for (int i = 0; i < insumoIds.length; i++) {
	            int insumoId = Integer.parseInt(insumoIds[i]);
	            double quantidadeUtilizada = Double.parseDouble(quantidadesInsumosUtilizadas[i]);
	            
	            ProdutoInsumo produtoInsumo = new ProdutoInsumo();
	            produtoInsumo.setInsumoId(insumoId);
	            produtoInsumo.setQuantidadeUtilizada(quantidadeUtilizada);
	            
	            produtoInsumos.add(produtoInsumo);
	        }
	        
	        List<ProdutoMaoObra> produtoMaosObra = new ArrayList<>();
	        for (int i = 0; i < insumoIds.length; i++) {
	            int maoObraId = Integer.parseInt(maoObraIds[i]);
	            double quantidadeUtilizada = Double.parseDouble(quantidadesMaoObraUtilizadas[i]);
	            
	            ProdutoInsumo produtoInsumo = new ProdutoInsumo();
	            produtoInsumo.setInsumoId(maoObraId);
	            produtoInsumo.setQuantidadeUtilizada(quantidadeUtilizada);
	            
	            produtoInsumos.add(produtoInsumo);
	        }

	        Produto produto = new Produto();
	        produto.setEmpreendimentoId(empreendimentoId);
	        produto.setId(produtoId);
	        produto.setNome(nome);
	        produto.setPrecoVenda(precoVenda);
	        produtoDAO.editarProduto(produto, produtoInsumos, produtoMaosObra); 
	        
	        mensagem = "Produto editado com sucesso!";

	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Erro ao editar produto: ", e);
	        mensagem = "Erro ao editar a produto.";
	    }

	    session.setAttribute("mensagem", mensagem);
	    response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	}
	
	private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int empreendimentoId = (Integer) session.getAttribute("id");
		String mensagem;

		try {
			String produtoIdStr = request.getParameter("id");
			int produtoId = (produtoIdStr == null || produtoIdStr.isEmpty()) ? 0 : Integer.parseInt(produtoIdStr);

			if (produtoId == 0) {
				mensagem = "Produto não encontrado para exclusão.";
				session.setAttribute("mensagem", mensagem);
				response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
				return;
			}

			boolean exclusao = produtoDAO.excluirProduto(produtoId, empreendimentoId);
			if (exclusao) {
				mensagem = "Produto excluído com sucesso.";
			} else {
				mensagem = "Não foi possível excluir o produto.";
			}

		} catch (NumberFormatException e) {
			logger.log(Level.WARNING,
					"Erro de formato ao converter ID do produto para exclusão: " + request.getParameter("id"), e);
			mensagem = "Falha ao excluir produto.";
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao excluir produto", e);
			mensagem = "Falha ao excluir produto.";
		}

		session.setAttribute("mensagem", mensagem);
		response.sendRedirect(request.getContextPath() + "/empreendimento/produto/listagem");
	}

}
