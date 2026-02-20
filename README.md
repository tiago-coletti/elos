# Elos - Plataforma de Gestão para Economia Solidária

O **Elos** é uma solução tecnológica web, simples e gratuita, desenvolvida para auxiliar empreendimentos de economia solidária a gerirem suas atividades de forma autônoma e eficiente.

O projeto nasce para resolver a dificuldade que muitos desses empreendimentos (pequenos produtores e artesãos) enfrentam no controle financeiro e na organização dos processos produtivos devido à falta de acesso à tecnologia. Adotando uma arquitetura **multi-tenancy**, o sistema permite que diversos grupos utilizem a plataforma simultaneamente, mantendo seus dados isolados e seguros.

## Objetivo e Fundamentação

Inspirado nos conceitos de Paul Singer, o Elos baseia-se nos princípios de cooperação, autogestão e inclusão social. O objetivo é oferecer uma ferramenta que priorize o bem-estar coletivo, fortaleça as comunidades locais e torne os processos de gestão mais acessíveis.

Além do impacto social direto, o projeto integra ensino, pesquisa e extensão, sendo utilizado na disciplina de Economia Solidária do IFSC – Campus Chapecó.

## Funcionalidades Principais

A interface foi desenhada de forma modular e acessível, com foco na usabilidade, através de uma abordagem participativa com a comunidade:

* **Dashboard Inteligente:** Relatórios financeiros e produtivos em tempo real para tomada de decisões estratégicas.
* **Gestão Integrada:** Controle centralizado de produtos, estoque, vendas, insumos, mão de obra e investimentos.
* **Controle de Estoque:** Alertas automáticos para produtos com baixo estoque.
* **Multi-tenancy:** Uma única instância da aplicação atende múltiplos empreendimentos de forma isolada.

## Tecnologias Utilizadas

O sistema foi estruturado para ser robusto e escalável:

* **Backend:** Java com Jakarta EE
* **Banco de Dados:** MySQL
* **Frontend:** HTML5, CSS3 e JavaScript

## Como Executar

**Pré-requisitos**
* Java JDK instalado (versão compatível com Jakarta EE).
* Servidor Tomcat.
* MySQL rodando na porta 3306.

**Passos**
1. Clone este repositório: `git clone https://github.com/seu-usuario/elos.git`
2. Configure as credenciais do banco de dados MySQL no arquivo de configuração do projeto.
3. Compile e construa o projeto utilizando o Maven.
4. Faça o deploy do arquivo `.war` gerado diretamente no Tomcat (seja no seu ambiente local ou em uma VPS).
5. Acesse a aplicação através do navegador.

## Autores

* Tiago Alfonso Neumann Coletti
* Lucas Eduardo Dacroce
* Orientação / Vínculo: Instituto Federal de Santa Catarina (IFSC)

## Licença

Este projeto é licenciado sob a [GNU General Public License v3.0 (GPLv3)](LICENSE).
Isso garante que o software será sempre livre e de código aberto para a comunidade.
