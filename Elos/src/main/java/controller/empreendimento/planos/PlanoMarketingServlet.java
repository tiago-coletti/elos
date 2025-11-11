package controller.empreendimento.planos;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.QuestionarioDAO;
import model.entity.Questionario;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/plano-marketing" })
public class PlanoMarketingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PlanoMarketingServlet.class.getName());
    
    private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
    private static final String NOME_QUESTIONARIO = "Plano de Marketing";

    public PlanoMarketingServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        int empreendimentoId = (Integer) session.getAttribute("id");
        
        try {
            Questionario questionario = questionarioDAO.obterOuCriarQuestionarioComPerguntas(NOME_QUESTIONARIO);
            if (questionario == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Questionário não encontrado.");
                return;
            }
            
            Map<Long, String> mapaRespostas = questionarioDAO.obterMapaRespostas(questionario.getId(), empreendimentoId);
            
            request.setAttribute("questionario", questionario);
            request.setAttribute("mapaRespostas", mapaRespostas);
            
            RequestDispatcher rd = request.getRequestDispatcher("/empreendimento/plano-marketing.jsp");
            rd.forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar dados do Plano de Marketing para o empreendimento", e);
            response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        int empreendimentoId = (Integer) session.getAttribute("id");
        
        Map<Long, String> respostasParaSalvar = new HashMap<>();
        
        try {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                
                if (paramName.startsWith("resposta_")) {
                    long perguntaId = Long.parseLong(paramName.substring(9));
                    String textoResposta = request.getParameter(paramName);
                    respostasParaSalvar.put(perguntaId, textoResposta);
                }
            }

            if (!respostasParaSalvar.isEmpty()) {
                questionarioDAO.salvarRespostas(empreendimentoId, respostasParaSalvar);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao processar salvamento do Plano de Marketing", e);
        }
        
        response.sendRedirect(request.getContextPath() + "/empreendimento/plano-marketing");
    }
}