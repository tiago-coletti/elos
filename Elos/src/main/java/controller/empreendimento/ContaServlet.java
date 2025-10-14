package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.EmpreendimentoDAO;
import model.entity.Aluno;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer empreendimentoId = (Integer) request.getSession().getAttribute("id");
        if (empreendimentoId == null) {
            response.sendRedirect(request.getContextPath() + "/empreendimento/login.html");
            return;
        }

        try {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer empreendimentoId = (Integer) request.getSession().getAttribute("id");
        if (empreendimentoId == null) {
            response.sendRedirect(request.getContextPath() + "/empreendimento/login.html");
            return;
        }
        
        String redirectURL = request.getContextPath() + "/empreendimento/conta";

        try {
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String numeroTelefone = request.getParameter("phoneNumber");
            String cidade = request.getParameter("city");
            String login = request.getParameter("loginEmpreendimento"); 
            String novaSenha = request.getParameter("novaSenha");
            String confirmarNovaSenha = request.getParameter("confirmarNovaSenha");
            
            String cursoGeralIdStr = request.getParameter("cursoGeralId");
            String anoSemestre = request.getParameter("anoSemestre");
            
            String[] alunoIds = request.getParameterValues("alunoId");
            String[] alunoNomes = request.getParameterValues("alunoNome");
            String[] alunoMatriculas = request.getParameterValues("alunoMatricula");
            
            Empreendimento empreendimentoExistente = empreendimentoDAO.obterEmpreendimentoCompletoPorId(empreendimentoId);
            
            if (empreendimentoExistente == null) {
                response.sendRedirect(redirectURL + "?erro=nao_encontrado");
                return;
            }
            
            String senhaHashParaAtualizacao = empreendimentoExistente.getSenha();
            
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                if (!novaSenha.equals(confirmarNovaSenha)) {
                    response.sendRedirect(redirectURL + "?erro=senhas_diferentes");
                    return;
                }
                senhaHashParaAtualizacao = PasswordUtils.hashPassword(novaSenha);
            }

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
                null,
                empreendimentoExistente.getDeletedAt()
            );

            boolean sucessoEmpreendimento = empreendimentoDAO.atualizarEmpreendimento(empreendimentoAtualizado);
            boolean sucessoAlunoInfo = true;
            boolean sucessoIntegrantes = true;

            if (sucessoEmpreendimento && empreendimentoExistente.isAluno()) {
                String cursoENUM = null;
                if ("1".equals(cursoGeralIdStr)) {
                    cursoENUM = "DESENVOLVIMENTO_SISTEMAS";
                } else if ("2".equals(cursoGeralIdStr)) {
                    cursoENUM = "ENERGIA_RENOVAVEIS";
                }

                sucessoAlunoInfo = empreendimentoDAO.atualizarEmpreendimentoAlunoInfo(
                    empreendimentoId, cursoENUM, anoSemestre);
                
                if (alunoIds != null && alunoNomes != null && alunoMatriculas != null) {
                    for (int i = 0; i < alunoIds.length; i++) {
                        int alunoId = Integer.parseInt(alunoIds[i]);
                        String alunoNome = alunoNomes[i];
                        String alunoMatricula = alunoMatriculas[i];

                        Aluno alunoParaAtualizar = new Aluno(alunoId, alunoNome, alunoMatricula, null);

                        if (!empreendimentoDAO.atualizarAluno(alunoParaAtualizar)) {
                            sucessoIntegrantes = false;
                            break;
                        }
                    }
                }
            }

            if (sucessoEmpreendimento && sucessoAlunoInfo && sucessoIntegrantes) {
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