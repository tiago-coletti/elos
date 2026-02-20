# Elos - Plataforma de Gestão para Economia Solidária

[cite_start]O **Elos** é uma solução tecnológica web, simples e gratuita, desenvolvida para auxiliar empreendimentos de economia solidária a gerirem suas atividades de forma autônoma e eficiente[cite: 4, 17]. 

[cite_start]O projeto nasce para resolver a dificuldade que muitos desses empreendimentos (pequenos produtores e artesãos) enfrentam no controle financeiro e na organização dos processos produtivos devido à falta de acesso à tecnologia[cite: 16, 41]. Adotando uma arquitetura **multi-tenancy**, o sistema permite que diversos grupos utilizem a plataforma simultaneamente, mantendo seus dados isolados e seguros.

## Objetivo e Fundamentação

[cite_start]Inspirado nos conceitos de Paul Singer, o Elos baseia-se nos princípios de cooperação, autogestão e inclusão social[cite: 30, 33]. [cite_start]O objetivo é oferecer uma ferramenta que priorize o bem-estar coletivo, fortaleça as comunidades locais e torne os processos de gestão mais acessíveis[cite: 34, 36].

[cite_start]Além do impacto social direto, o projeto integra ensino, pesquisa e extensão, sendo utilizado na disciplina de Economia Solidária do IFSC – Campus Chapecó[cite: 43, 119].

## Funcionalidades Principais

[cite_start]A interface foi desenhada de forma modular e acessível, com foco na usabilidade, através de uma abordagem participativa com a comunidade[cite: 49, 69]:

* [cite_start]**Dashboard Inteligente:** Relatórios financeiros e produtivos em tempo real para tomada de decisões estratégicas[cite: 68].
* [cite_start]**Gestão Integrada:** Controle centralizado de produtos, estoque, vendas, insumos, mão de obra e investimentos[cite: 67].
* [cite_start]**Controle de Estoque:** Alertas automáticos para produtos com baixo estoque[cite: 101].
* **Multi-tenancy:** Uma única instância da aplicação atende múltiplos empreendimentos de forma isolada.

## Tecnologias Utilizadas

O sistema foi estruturado para ser robusto e escalável:

* [cite_start]**Backend:** Java com Jakarta EE[cite: 53, 126].
* [cite_start]**Banco de Dados:** MySQL[cite: 53].
* [cite_start]**Frontend:** HTML5, CSS3 e JavaScript[cite: 51].

## Como Executar

### Pré-requisitos
* Java JDK instalado (versão compatível com Jakarta EE).
* Servidor de aplicação (ex: Tomcat, WildFly ou GlassFish).
* MySQL rodando na porta 3306.

### Passos
1. Clone este repositório:
   `git clone https://github.com/seu-usuario/elos.git`
2. Configure as credenciais do banco de dados MySQL no arquivo de configuração do projeto (ex: `persistence.xml` ou equivalente do seu setup).
3. Compile e construa o projeto utilizando o Maven ou Gradle.
4. Faça o deploy do arquivo `.war` gerado no seu servidor de aplicação.
5. Acesse a aplicação através do navegador no `localhost`.

## Autores
* [cite_start]Tiago Alfonso Neumann Coletti 
* [cite_start]Lucas Eduardo Dacroce 
* [cite_start]Orientação / Vínculo: Instituto Federal de Santa Catarina (IFSC) 

## Licença

Este projeto é licenciado sob a **GNU General Public License v3.0 (GPLv3)**.

Isso significa que você tem a liberdade de usar, estudar, compartilhar e modificar este software. No entanto, qualquer versão modificada ou trabalho derivado deve, obrigatoriamente, ser distribuído sob a mesma licença GPLv3, mantendo o código aberto e gratuito para a comunidade.
