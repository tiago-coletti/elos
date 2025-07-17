package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebFilter(urlPatterns = {""})
public class AuthEmpreendimentoFilter extends HttpFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false); 
        
        String path = req.getRequestURI();
        // Libera o LoginServlet sem checar sessão
        if (path.endsWith("/empreendimento/LoginServlet")) {
            chain.doFilter(req, res);
            return;
        }
        
        // Verifica se está logado (como empreendimento)
        if (session == null || session.getAttribute("idEmpreendimento") == null) {
            res.sendRedirect(req.getContextPath() + "/index.html?error=4");
            return;
        }

		// Segue com a requisição
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

}