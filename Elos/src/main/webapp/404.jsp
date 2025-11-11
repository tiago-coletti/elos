<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Página Não Encontrada</title>
    <style>
        body { font-family: Arial, sans-serif; text-align: center; padding-top: 50px; }
        h1 { font-size: 72px; margin: 0; }
        p { font-size: 24px; }
        a { color: #007bff; text-decoration: none; }
    </style>
</head>
<body>
    <h1>404</h1>
    <p>Ops! A página que você está procurando não existe.</p>
    <p><a href="${pageContext.request.contextPath}/">Voltar para a página inicial</a></p>
</body>
</html>