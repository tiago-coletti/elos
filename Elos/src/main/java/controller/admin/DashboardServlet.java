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

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private AdminDAO adminDAO;

    public DashboardServlet() {
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

        try {
            List<Map<String, Object>> cardsData = adminDAO.obterDadosCardsDashboard();
            
            request.setAttribute("cardsData", cardsData);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/dashboard.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao carregar o dashboard.");
        }
    }
}