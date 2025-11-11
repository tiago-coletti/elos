package controller.admin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.AdminDAO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard-ifsc")
public class DashboardIfscServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AdminDAO adminDAO;

    public DashboardIfscServlet() {
        super();
        this.adminDAO = new AdminDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=sessao_expirada");
            return;
        }
        
        String filtroSemestre = request.getParameter("semestre");
        String filtroCurso = request.getParameter("curso");

        try {
            List<Map<String, Object>> cardsData = adminDAO.obterDadosCardsIfsc(filtroSemestre, filtroCurso);
            List<String> listaSemestres = adminDAO.obterSemestresAlunos();
            
            request.setAttribute("cardsData", cardsData);
            request.setAttribute("listaSemestres", listaSemestres);
            request.setAttribute("filtroSemestre", filtroSemestre);
            request.setAttribute("filtroCurso", filtroCurso);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard-ifsc.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao carregar o dashboard IFSC.");
        }
    }
}