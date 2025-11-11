<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />
  <title>Elos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/empreendimento/assets/css/navbar.css" />
  
  <style>
    .construction-notice {
        text-align: center;
        padding: 50px 20px;
        margin-top: 30px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        transition: background-color 0.3s, box-shadow 0.3s;
    }
    .construction-notice .bx {
        font-size: 60px;
        color: #e67e22; 
        margin-bottom: 20px;
    }
    .construction-notice h3 {
        font-size: 1.5rem;
        color: #333;
        margin-bottom: 10px;
        transition: color 0.3s;
    }
    .construction-notice p {
        font-size: 1rem;
        color: #666;
        transition: color 0.3s;
    }

    body.dark .construction-notice {
        background-color: #1e1e1e;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }
    body.dark .construction-notice h3 {
        color: #f5f5f5;
    }
    body.dark .construction-notice p {
        color: #a0a0a0;
    }
    body.dark .construction-notice .bx {
        color: #f39c12; 
    }
  </style>
  </head>

<body>
	<%@ include file="/empreendimento/shared/navbar.jspf"%>

	<main class="container">
	    <div class="dashboard-title">
	        <h2>Em Desenvolvimento</h2>
	    </div>
	
	    <section class="construction-notice">
            <i class='bx bx-hard-hat'></i>
            <h3>Página em Construção</h3>
            <p>Esta funcionalidade ainda está sendo desenvolvida. <br/> Pedimos desculpas pelo inconveniente e voltaremos em breve!</p>
	    </section>
	
	</main>
    
    <script src="${pageContext.request.contextPath}/empreendimento/assets/js/navbar.js"></script>
</body>
</html>