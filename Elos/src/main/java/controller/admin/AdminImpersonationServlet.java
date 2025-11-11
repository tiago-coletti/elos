package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/abrir-empreendimento")
public class AdminImpersonationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AdminImpersonationServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.html?erro=nao_autorizado");
            return;
        }

        String empreendimentoIdStr = request.getParameter("id");
        
        if (empreendimentoIdStr == null || empreendimentoIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?erro=id_invalido");
            return;
        }

        try {
            int empreendimentoId = Integer.parseInt(empreendimentoIdStr);

            session.setAttribute("id", empreendimentoId);
            
            response.sendRedirect(request.getContextPath() + "/empreendimento/dashboard-principal");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?erro=id_invalido");
        }
    }
}