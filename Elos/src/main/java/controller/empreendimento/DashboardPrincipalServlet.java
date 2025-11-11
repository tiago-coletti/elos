package controller.empreendimento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import model.dao.VendaDAO;
import model.dao.InsumoDAO;
import model.dao.EmpreendimentoDAO; 

@WebServlet("/empreendimento/dashboard-principal")
public class DashboardPrincipalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
		
	public DashboardPrincipalServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		LocalDateTime agora = LocalDateTime.now();
		LocalTime hora = agora.toLocalTime();
		
		DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yy");
		DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");
		
		request.setAttribute("dataAtual", agora.format(formatadorData));
		request.setAttribute("horaAtual", agora.format(formatadorHora));
		
		String saudacao;
		LocalTime inicioManha = LocalTime.of(4, 30);
		LocalTime inicioTarde = LocalTime.of(12, 0);
		LocalTime inicioNoite = LocalTime.of(18, 30);

		if (!hora.isBefore(inicioManha) && hora.isBefore(inicioTarde)) {
			saudacao = "Bom dia!";
		} else if (!hora.isBefore(inicioTarde) && hora.isBefore(inicioNoite)) {
			saudacao = "Boa tarde!";
		} else {
			saudacao = "Boa noite!";
		}
		
		request.setAttribute("saudacao", saudacao);
		HttpSession session = request.getSession(false);
		Integer empreendimentoIdObj = null; 
		if (session != null) {
			empreendimentoIdObj = (Integer) session.getAttribute("id");
		}
		if (empreendimentoIdObj == null) {
			response.sendRedirect(request.getContextPath() + "/empreendimento/login");
			return;
		}
		int empreendimentoId = empreendimentoIdObj; 
		
		// DAOs
		VendaDAO vendaDAO = new VendaDAO();
		InsumoDAO insumoDAO = new InsumoDAO();
		EmpreendimentoDAO empreendimentoDAO = new EmpreendimentoDAO();
		
		// Cálculos
		double totalVendidoMes = vendaDAO.calcularTotalVendidoPorPeriodo(empreendimentoId, "mes");
		double totalValorInsumos = insumoDAO.calcularValorTotalEstoque(empreendimentoId);
		double saldoAtual = empreendimentoDAO.obterSaldo(empreendimentoId);
		
		// Formatação
		Locale ptBR = new Locale("pt", "BR");
		NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(ptBR);
		
		// Envio para o JSP
		request.setAttribute("totalVendido", formatadorMoeda.format(totalVendidoMes));
		request.setAttribute("totalInsumos", formatadorMoeda.format(totalValorInsumos));
		request.setAttribute("saldoAtual", formatadorMoeda.format(saldoAtual));
		
		RequestDispatcher rd = request.getRequestDispatcher("dashboard-principal.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}