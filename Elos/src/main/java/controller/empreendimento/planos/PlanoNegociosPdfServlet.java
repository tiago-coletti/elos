package controller.empreendimento.planos;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.QuestionarioDAO;
import model.entity.Pergunta;
import model.entity.Questionario;

import org.xhtmlrenderer.pdf.ITextRenderer; // Flying Saucer import

import java.io.ByteArrayOutputStream; // Stream para PDF
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/empreendimento/plano-negocios/pdf")
public class PlanoNegociosPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PlanoNegociosPdfServlet.class.getName());
    
    private QuestionarioDAO questionarioDAO = new QuestionarioDAO();
    private static final String NOME_QUESTIONARIO = "Plano de Negócios";
    
    // CAMINHO DO TEMPLATE CORRIGIDO AQUI
    private static final String TEMPLATE_PATH = "/empreendimento/templates/questionario-template.html"; 

    public PlanoNegociosPdfServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sessão inválida.");
            return;
        }
        int empreendimentoId = (Integer) session.getAttribute("id");
        
        ByteArrayOutputStream pdfOutputStream = null; 

        try {
            Questionario questionario = questionarioDAO.obterOuCriarQuestionarioComPerguntas(NOME_QUESTIONARIO);
            if (questionario == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Questionário não encontrado.");
                return;
            }
            Map<Long, String> mapaRespostas = questionarioDAO.obterMapaRespostas(questionario.getId(), empreendimentoId);
            
            String htmlContent = carregarTemplate();
            htmlContent = preencherTemplate(htmlContent, questionario, mapaRespostas);
            
            pdfOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            
             String baseUrl = getServletContext().getRealPath("/");
             if (baseUrl != null) {
                 renderer.setDocumentFromString(htmlContent, "file:///" + baseUrl);
             } else {
                 renderer.setDocumentFromString(htmlContent); // Sem base URL explícita
             }

            renderer.layout();
            renderer.createPDF(pdfOutputStream);
            renderer.finishPDF();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"plano_negocios.pdf\"");
            response.setContentLength(pdfOutputStream.size());

            response.getOutputStream().write(pdfOutputStream.toByteArray());
            response.getOutputStream().flush();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar PDF do Plano de Negócios", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao gerar PDF.");
        } finally {
            if (pdfOutputStream != null) {
                try {
                    pdfOutputStream.close();
                } catch (IOException e) { /* Ignorar erro no fechamento */ }
            }
        }
    }

    private String carregarTemplate() throws IOException {
        InputStream inputStream = getServletContext().getResourceAsStream(TEMPLATE_PATH);
        if (inputStream == null) {
            throw new IOException("Template não encontrado: " + TEMPLATE_PATH);
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String preencherTemplate(String htmlTemplate, Questionario questionario, Map<Long, String> mapaRespostas) {
        String conteudoFinal = htmlTemplate.replace("##QUESTIONARIO_NOME##", questionario.getNome());
        
        StringBuilder perguntasHtml = new StringBuilder();
        List<Pergunta> perguntasOrdenadas = questionario.getPerguntas(); 

        for (Pergunta p : perguntasOrdenadas) {
            String resposta = mapaRespostas.getOrDefault(p.getId(), "");
            perguntasHtml.append("<div class=\"pergunta-bloco\">");
            perguntasHtml.append("<p class=\"pergunta-texto\">").append(p.getOrdem()).append(". ").append(escapeHtml(p.getTextoPergunta())).append("</p>");
            if (resposta != null && !resposta.isEmpty()) {
                perguntasHtml.append("<p class=\"resposta-texto\">").append(escapeHtml(resposta)).append("</p>");
            } else {
                perguntasHtml.append("<p class=\"sem-resposta\">Não respondido</p>");
            }
            perguntasHtml.append("</div>");
        }
        
        return conteudoFinal.replace("##PERGUNTAS_RESPOSTAS##", perguntasHtml.toString());
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}