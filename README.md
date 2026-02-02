# Biscato - Aplicativo Freelancer Aplicado ao Contexto Angolano

## Resumo
O **Biscato** é uma solução móvel inovadora desenvolvida para dinamizar o mercado de prestação de serviços em Angola. O aplicativo atua como um intermediário digital seguro e eficiente, conectando clientes que necessitam de serviços rápidos e confiáveis a profissionais autônomos (freelancers) qualificados. O nome "Biscato" remete à expressão popular angolana para trabalhos temporários ou extras, valorizando a cultura local e a força de trabalho informal.

## Descrição Detalhada do Sistema

### 1. Contextualização e Problema
No cenário econômico angolano, uma grande parcela da população atua no mercado informal ou busca complementação de renda através da prestação de serviços. Simultaneamente, famílias e empresas enfrentam dificuldades para encontrar profissionais de confiança (como eletricistas, encanadores, técnicos de TI, professores particulares) de forma rápida e segura. A falta de uma plataforma centralizada gera insegurança e ineficiência nessas transações.

O **Biscato** resolve esse problema digitalizando a confiança e a busca por serviços, adaptando-se às limitações de conectividade locais com uma arquitetura robusta.

### 2. Funcionalidades Principais

#### Para o Cliente (Contratante)
*   **Busca Inteligente:** Localização de prestadores de serviço por categoria (Obras, Limpeza, Tecnologia, Educação, Mecânica, etc.) e proximidade.
*   **Gestão de Pedidos:** Criação de solicitações detalhadas com descrição, fotos e orçamento previsto.
*   **Avaliação e Confiança:** Sistema de *rating* (avaliação) para classificar a qualidade do serviço prestado, promovendo a meritocracia.
*   **Histórico:** Acesso rápido aos serviços anteriores e profissionais favoritos.

#### Para o Freelancer (Prestador)
*   **Perfil Profissional:** Vitrine digital para expor habilidades, portfólio de trabalhos realizados e certificações.
*   **Oportunidades em Tempo Real:** Recebimento de notificações sobre novos pedidos na sua área de atuação.
*   **Gestão de Carreira:** Controle de serviços aceitos, pendentes e finalizados.

### 3. Arquitetura e Tecnologia
O sistema foi construído utilizando as tecnologias mais modernas de desenvolvimento Android nativo, garantindo performance e escalabilidade.

*   **Linguagem:** Kotlin.
*   **Interface (UI):** Jetpack Compose (Design moderno, responsivo e intuitivo).
*   **Arquitetura:** MVVM (Model-View-ViewModel) seguindo os princípios de Clean Architecture.
*   **Organização de Código:** Modularização por funcionalidades (`ui.auth`, `ui.client`, `ui.freelancer`) para fácil manutenção.

### 4. Diferenciais Técnicos (Resiliência e Contexto Local)
Considerando a instabilidade da conexão de internet em algumas regiões de Angola, o sistema implementa uma estratégia **Offline-First**:

*   **Persistência Local (Room Database):** O aplicativo armazena dados críticos localmente. Funcionalidades essenciais como Login (com credenciais em cache), visualização de histórico e consulta de perfil funcionam mesmo sem internet.
*   **Sincronização Inteligente:** Integração com API REST via **Retrofit** e **Coroutines**. O app tenta conectar ao servidor; em caso de falha, opera com os dados locais e sincroniza quando a conexão é restabelecida.
*   **Firebase:** Integração para análise de dados (Analytics) e suporte à autenticação robusta.

### 5. Identidade Visual
A interface foi projetada para ser acolhedora e vibrante, utilizando uma paleta de cores que transmite energia e seriedade:
*   **Cor Primária:** Vermelho (`RedPrimary`) - Energia e urgência.
*   **Cor Secundária:** Vinho (`WineSecondary`) - Sofisticação e confiança.

### 6. Conclusão
O **Biscato** não é apenas um aplicativo, é uma ferramenta de inclusão digital e fomento econômico, facilitando a vida dos angolanos e profissionalizando o setor de serviços informais.
