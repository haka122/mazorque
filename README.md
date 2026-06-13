# Consulta CEP

## Descrição
Aplicativo Android simples para consultar endereços brasileiros a partir de um CEP. O app valida a entrada do usuário, consulta uma API pública e exibe o resultado de forma organizada na tela.

## API utilizada
- Nome da API: ViaCEP
- Endpoint utilizado: `https://viacep.com.br/ws/{cep}/json/`
- Exemplo de URL consultada: `https://viacep.com.br/ws/01001000/json/`
- Principais dados retornados: CEP, logradouro, bairro, localidade, UF e DDD.

## Funcionalidades
- Entrada de dados pelo usuário
- Validação de campo vazio
- Validação de CEP com 8 números
- Consulta a uma API pública
- Exibição dos dados retornados
- Tratamento básico de erro para CEP inválido, CEP não encontrado e falha de conexão

## Tecnologias utilizadas
- Kotlin
- Android Studio
- XML
- HttpURLConnection
- API pública ViaCEP

## Permissões utilizadas
O aplicativo utiliza a permissão INTERNET para realizar requisições à API pública.

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Como executar o projeto
1. Clonar este repositório.
2. Abrir a pasta do projeto no Android Studio.
3. Aguardar a sincronização do Gradle.
4. Executar o app em um emulador ou dispositivo físico.
5. Informar um CEP válido, como `01001000`, e tocar em **Consultar CEP**.

