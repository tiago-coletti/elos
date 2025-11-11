package controller.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.QuestionarioDAO;
import model.entity.Pergunta;
import model.entity.Questionario;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { 
    "/admin/plano-marketing/listagem", 
    "/admin/plano-marketing/incluir",
    "/admin/plano-marketing/editar",
    "/admin/plano-marketing/excluir"
})
public class PlanoMarketingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PlanoMarketingServlet.class.getName());
    
    private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
    private static final String NOME_QUESTIONARIO = "Plano de Marketing";
    private static final String URL_LISTAGEM = "/admin/plano-marketing/listagem";

    public PlanoMarketingServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/admin/plano-marketing/listagem":
                visualizarListagem(request, response);
                break;
            case "/admin/plano-marketing/incluir":
                visualizarInclusao(request, response);
                break;
            case "/admin/plano-marketing/editar":
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
            case "/admin/plano-marketing/incluir":
                processarInclusao(request, response);
                break;
            case "/admin/plano-marketing/editar":
                processarEdicao(request, response);
                break;
            case "/admin/plano-marketing/excluir":
                processarExclusao(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Rota não reconhecida.");
        }
    }

    private void visualizarListagem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=sessao_expirada");
            return;
        }
        
        try {
            Questionario questionario = questionarioDAO.obterOuCriarQuestionarioComPerguntas(NOME_QUESTIONARIO);
            request.setAttribute("questionario", questionario);
            
            RequestDispatcher rd = request.getRequestDispatcher("/admin/plano-marketing/listagem.jsp");
            rd.forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar dados do questionário", e);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        }
    }

    private void visualizarInclusao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=sessao_expirada");
            return;
        }
        
        try {
            String questionarioId = request.getParameter("qId");
            String questionarioNome = request.getParameter("qNome");
            
            request.setAttribute("questionarioId", questionarioId);
            request.setAttribute("questionarioNome", questionarioNome);
            
            RequestDispatcher rd = request.getRequestDispatcher("/admin/plano-marketing/incluir.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar página de inclusão de pergunta", e);
            response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
        }
    }
    
    private void visualizarEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=sessao_expirada");
            return;
        }
        
        try {
            long perguntaId = Long.parseLong(request.getParameter("id"));
            Pergunta pergunta = questionarioDAO.obterPerguntaPorId(perguntaId);
            
            if (pergunta == null) {
                response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
                return;
            }
            
            request.setAttribute("pergunta", pergunta);
            request.setAttribute("questionarioNome", NOME_QUESTIONARIO);
            
            RequestDispatcher rd = request.getRequestDispatcher("/admin/plano-marketing/editar.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar página de edição de pergunta", e);
            response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
        }
    }

    private void processarInclusao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            long questionarioId = Long.parseLong(request.getParameter("questionarioId"));
            String textoPergunta = request.getParameter("textoPergunta");
            String tipoPergunta = request.getParameter("tipoPergunta");
            int ordem = Integer.parseInt(request.getParameter("ordem"));

            Pergunta novaPergunta = new Pergunta();
            novaPergunta.setQuestionarioId(questionarioId);
            novaPergunta.setTextoPergunta(textoPergunta);
            novaPergunta.setTipoPergunta(tipoPergunta);
            novaPergunta.setOrdem(ordem);

            questionarioDAO.incluirPergunta(novaPergunta);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao processar inclusão de pergunta", e);
        }
        
        response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
    }
    
    private void processarEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            long perguntaId = Long.parseLong(request.getParameter("perguntaId"));
            long questionarioId = Long.parseLong(request.getParameter("questionarioId"));
            String textoPergunta = request.getParameter("textoPergunta");
            String tipoPergunta = request.getParameter("tipoPergunta");
            int ordem = Integer.parseInt(request.getParameter("ordem"));

            Pergunta pergunta = new Pergunta();
            pergunta.setId(perguntaId);
            pergunta.setQuestionarioId(questionarioId);
            pergunta.setTextoPergunta(textoPergunta);
            pergunta.setTipoPergunta(tipoPergunta);
            pergunta.setOrdem(ordem);

            questionarioDAO.atualizarPergunta(pergunta);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao processar edição de pergunta", e);
        }
        
        response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
    }
    
    private void processarExclusao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            long perguntaId = Long.parseLong(request.getParameter("perguntaId"));
            questionarioDAO.excluirPergunta(perguntaId);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao processar exclusão de pergunta", e);
        }
        
        response.sendRedirect(request.getContextPath() + URL_LISTAGEM);
    }
}