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
import model.entity.EmpreendimentoAluno;
import util.PasswordUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = { "/empreendimento/conta", "/empreendimento/atualizarConta" })
public class ContaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContaServlet.class.getName());

    private final EmpreendimentoDAO empreendimentoDAO = new EmpreendimentoDAO();

    @Override
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
                response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal?erro=conta_nao_encontrada");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar a página de conta: ", e);
            response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal?erro=carregamento_conta");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer empreendimentoId = (Integer) request.getSession().getAttribute("id");
        if (empreendimentoId == null) {
            response.sendRedirect(request.getContextPath() + "/empreendimento/login.html");
            return;
        }
        
        String redirectURL = request.getContextPath() + "/empreendimento/conta";
        boolean sucessoGeral = true;

        try {
            Empreendimento empreendimentoExistente = empreendimentoDAO.obterEmpreendimentoCompletoPorId(empreendimentoId);
            if (empreendimentoExistente == null) {
                response.sendRedirect(redirectURL + "?erro=nao_encontrado");
                return;
            }

            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String numeroTelefone = request.getParameter("phoneNumber");
            String cidade = request.getParameter("city");
            String novaSenha = request.getParameter("novaSenha");
            String confirmarNovaSenha = request.getParameter("confirmarNovaSenha");
            
            String senhaHash = empreendimentoExistente.getSenha();
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                if (!novaSenha.equals(confirmarNovaSenha)) {
                    response.sendRedirect(redirectURL + "?erro=senhas_diferentes");
                    return;
                }
                senhaHash = PasswordUtils.hashPassword(novaSenha);
            }

            Empreendimento empreendimentoAtualizado = new Empreendimento(
                empreendimentoId, nome, email, senhaHash, empreendimentoExistente.getLogin(), 
                empreendimentoExistente.getTipo(), numeroTelefone, cidade, 
                empreendimentoExistente.getCreatedAt(), null, empreendimentoExistente.getDeletedAt()
            );

            if (!empreendimentoDAO.atualizarEmpreendimento(empreendimentoAtualizado)) {
                sucessoGeral = false;
            }

            if (empreendimentoExistente.isAluno() && sucessoGeral) {
                String cursoGeralIdStr = request.getParameter("cursoGeralId");
                String anoSemestre = request.getParameter("anoSemestre");
                String cursoENUM = "1".equals(cursoGeralIdStr) ? "DESENVOLVIMENTO_SISTEMAS" : "ENERGIA_RENOVAVEIS";

                if (!empreendimentoDAO.atualizarEmpreendimentoAlunoInfo(empreendimentoId, cursoENUM, anoSemestre)) {
                    sucessoGeral = false;
                }

                String idsParaRemoverStr = request.getParameter("alunosParaRemover");
                if (idsParaRemoverStr != null && !idsParaRemoverStr.isEmpty()) {
                    String[] idsParaRemover = idsParaRemoverStr.split(",");
                    for (String idStr : idsParaRemover) {
                        if (!idStr.isEmpty()) {
                            int alunoId = Integer.parseInt(idStr);
                            if (!empreendimentoDAO.removerAssociacaoAluno(empreendimentoId, alunoId)) {
                                sucessoGeral = false;
                            }
                        }
                    }
                }

                String[] alunoIds = request.getParameterValues("alunoId");
                String[] alunoNomes = request.getParameterValues("alunoNome");
                String[] alunoMatriculas = request.getParameterValues("alunoMatricula");

                if (alunoIds != null) {
                    for (int i = 0; i < alunoIds.length; i++) {
                        int alunoId = Integer.parseInt(alunoIds[i]);
                        String alunoNome = alunoNomes[i];
                        String alunoMatricula = alunoMatriculas[i];

                        if (alunoId > 0) {
                            Aluno alunoParaAtualizar = new Aluno(alunoId, alunoNome, alunoMatricula, null);
                            if (!empreendimentoDAO.atualizarAluno(alunoParaAtualizar)) {
                                sucessoGeral = false;
                            }
                        } else {
                            Aluno novoAluno = new Aluno(0, alunoNome, alunoMatricula, cursoENUM);
                            int novoAlunoId = empreendimentoDAO.incluirAluno(novoAluno);
                            
                            if (novoAlunoId > 0) {
                                EmpreendimentoAluno novaAssociacao = new EmpreendimentoAluno(empreendimentoId, novoAlunoId, anoSemestre);
                                if (!empreendimentoDAO.incluirEmpreendimentoAluno(novaAssociacao)) {
                                    sucessoGeral = false;
                                }
                            } else {
                                sucessoGeral = false;
                            }
                        }
                    }
                }
            }

            if (sucessoGeral) {
                response.sendRedirect(redirectURL + "?sucesso=true");
            } else {
                response.sendRedirect(redirectURL + "?erro=falha_atualizacao");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro fatal ao atualizar conta para o ID: " + empreendimentoId, e);
            response.sendRedirect(redirectURL + "?erro=erro_interno");
        }
    }
}