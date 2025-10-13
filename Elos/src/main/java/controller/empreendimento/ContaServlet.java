package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.EmpreendimentoDAO;
import model.entity.Empreendimento;
import util.PasswordUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/conta", "/empreendimento/atualizarConta" })
public class ContaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContaServlet.class.getName());

    private final EmpreendimentoDAO empreendimentoDAO = new EmpreendimentoDAO();

    // ----------------------------------------------------------------------
    // GET: CARREGAR DADOS DA CONTA
    // ----------------------------------------------------------------------
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer empreendimentoId = (Integer) request.getSession().getAttribute("id");
        if (empreendimentoId == null) {
            response.sendRedirect(request.getContextPath() + "/empreendimento/login.html");
            return;
        }

        try {
            // Usa o novo método para carregar dados completos, incluindo dados de aluno
            Empreendimento empreendimento = empreendimentoDAO.obterEmpreendimentoCompletoPorId(empreendimentoId);

            if (empreendimento != null) {
                request.setAttribute("empreendimento", empreendimento);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/empreendimento/conta.jsp");
                dispatcher.forward(request, response);
            } else {
                logger.log(Level.WARNING, "Empreendimento ID não encontrado: " + empreendimentoId);
                response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal?erro=conta_nao_encontrada");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar a página de conta para o ID: " + empreendimentoId, e);
            response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal?erro=carregamento_conta");
        }
    }

    // ----------------------------------------------------------------------
    // POST: ATUALIZAR DADOS DA CONTA
    // ----------------------------------------------------------------------
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer empreendimentoId = (Integer) request.getSession().getAttribute("id");
        if (empreendimentoId == null) {
            response.sendRedirect(request.getContextPath() + "/empreendimento/login.html");
            return;
        }
        
        String redirectURL = request.getContextPath() + "/empreendimento/conta";

        try {
            // 1. Coleta de Parâmetros
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String numeroTelefone = request.getParameter("phoneNumber");
            String cidade = request.getParameter("city");
            String login = request.getParameter("loginEmpreendimento"); 
            String novaSenha = request.getParameter("novaSenha");
            String confirmarNovaSenha = request.getParameter("confirmarNovaSenha");
            
            String cursoGeralIdStr = request.getParameter("cursoGeralId");
            String anoSemestre = request.getParameter("anoSemestre");
            
            // 2. Obter dados existentes
            Empreendimento empreendimentoExistente = empreendimentoDAO.obterEmpreendimentoCompletoPorId(empreendimentoId);
            
            if (empreendimentoExistente == null) {
                response.sendRedirect(redirectURL + "?erro=nao_encontrado");
                return;
            }
            
            // 3. Validação e Preparação da Senha
            String senhaHashParaAtualizacao = empreendimentoExistente.getSenha();
            
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                if (!novaSenha.equals(confirmarNovaSenha)) {
                    response.sendRedirect(redirectURL + "?erro=senhas_diferentes");
                    return;
                }
                senhaHashParaAtualizacao = PasswordUtils.hashPassword(novaSenha);
            }

            // 4. Cria a Entidade para atualização (mantendo os dados que não mudam)
            Empreendimento empreendimentoAtualizado = new Empreendimento(
                empreendimentoId, 
                nome, 
                email, 
                senhaHashParaAtualizacao, 
                login, 
                empreendimentoExistente.getTipo(), 
                numeroTelefone, 
                cidade, 
                empreendimentoExistente.getCreatedAt(), 
                null, // Deixe o DAO atualizar este campo
                empreendimentoExistente.getDeletedAt()
            );

            // 5. Atualiza o Empreendimento (Dados principais)
            boolean sucessoEmpreendimento = empreendimentoDAO.atualizarEmpreendimento(empreendimentoAtualizado);
            boolean sucessoAluno = true;

            // 6. Lógica para Empreendimento Solidário de Alunos
            if (sucessoEmpreendimento && empreendimentoExistente.isAluno()) {
                // Mapeia o ID do curso para o valor ENUM
                String cursoENUM = null;
                if ("1".equals(cursoGeralIdStr)) {
                    cursoENUM = "DESENVOLVIMENTO_SISTEMAS";
                } else if ("2".equals(cursoGeralIdStr)) {
                    cursoENUM = "ENERGIA_RENOVAVEIS";
                }

                // Novo método no DAO para atualizar as informações de Aluno
                 sucessoAluno = empreendimentoDAO.atualizarEmpreendimentoAlunoInfo(
                    empreendimentoId, cursoENUM, anoSemestre);
            }

            // 7. Resposta Final
            if (sucessoEmpreendimento && sucessoAluno) {
                response.sendRedirect(redirectURL + "?sucesso=true");
            } else {
                response.sendRedirect(redirectURL + "?erro=falha_atualizacao");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar conta para o ID: " + empreendimentoId, e);
            response.sendRedirect(redirectURL + "?erro=erro_interno");
        }
    }
}