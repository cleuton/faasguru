# faasguru
Software, tips and labs about FaaS and Serverless technology

**Cleuton Sampaio** 

![](../../faasguru1.jpeg)

![](../../images/sig-verifier.png)

# Crie uma função Java

Vamos criar uma função Java e servi-la com o **Open FaaS**, totalmente **Serverless**. Esta função é aquela que usei no primeiro artigo, quando a instalei no **AWS Lambda**. Ela confere a assinatura digital de um texto.

O [**código-fonte**](https://github.com/cleuton/faasguru/blob/master/openfaas-java/src/main/java/com/openfaas/function/Handler.java) demonstra o quão simples é criar uma função para o ambiente Open FaaS: 

```
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
...

public class Handler implements com.openfaas.model.IHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        String [] parameters = req.getBody().split(":");
        System.out.println("Parameters: " + req.getBody());
        String returnValue = "";
        try {
            returnValue =  "Signature is " + verify(parameters[1],parameters[0],"*","meucertificado","teste001");
            System.out.println("Return: " + returnValue);
            res.setBody(returnValue);
        } catch (Exception ex) {
            res.setBody("Exception :" + ex.getMessage());
        }
	    return res;
    }
...
```

Como pode ver, a função recebe uma instância de [**IRequest**](https://github.com/openfaas/templates/blob/master/template/java8/model/src/main/java/com/openfaas/model/IRequest.java) e devolve uma instância de [**IResponse**](https://github.com/openfaas/templates/blob/master/template/java8/model/src/main/java/com/openfaas/model/IResponse.java). Ela deve implementar a interface [**com.openfaas.model.IHandler**](https://github.com/openfaas/templates/blob/master/template/java8/model/src/main/java/com/openfaas/model/IHandler.java) que só tem o método **handle()**. 

Realmente, é muito simples!

Eu só **enxertei** o código do Signature.Java nela e pronto!

## Como criar a função

Primeiramente, você deve criar uma função, baixando o template Java (pode ser 8 ou 12): 

```
faas-cli new --lang java8 signature
```

Depois, você "enxerta" o código-fonte com a sua classe ou o seu método. Foi o que fiz. Quando estiver pronto, copie os arquivos [**build.gradle**](../../openfaas-java/build.gradle) e [**settings.gradle**](../../openfaas-java/settings.gradle) por cima dos que ele gerou. Por que? Bom, primeiramente, eu estou usando duas bibliotecas do **Maven** e preciso fazer umas modificações no build.gradle: 

```
repositories{
    mavenCentral()
}

dependencies {
    ...    
    implementation 'commons-codec:commons-codec:1.13'
    compile group: 'commons-io', name: 'commons-io', version: '2.6'
```

É para baixar o commons-codec e o commons-io que estou usando.

Depois, é preciso alterar o nome da função no "rootProject.name", dentro do settings.gradle.

**Nota:** A decisão da equipe OpenFaaS de usar o **Gradle** é lamentável. Ficam tentando **empurrar** uma ferramenta, quando o padrão para Java é o **Maven**. Realmente lamentável.

Ok, agora é o momento de fazer o build: 

```
sudo faas-cli build -f signature.yml
```

Se tudo der certo, faça o Deploy: 

```
faas-cli deploy -f signature.yml
```

E pode executar a função via Console ou invocar usando a URL da função: 

![](../../images/openfaas-java.png)

Ou você pode fazer um "post" com **CURL**: 

```
curl -d "This is a sample textfile:0badcb092f69338ce10f53fb25539fba6ce128751f645fb0bc0180cdd4be0d0ad3eaaffa6ca3510c6044cede112674d90b226eede686592c580cf2d0cb6176118c0eefd5fbf23f3d85e151d516646e483f2aca455f5856b402f50f2e70bf61babc65133da7d9b5576916f9526bb7d419c657e30d61ff3e54212358ee972121e8ec9f9b436c365c7c9930284487bfa457932fefd7dbc3f323252c45b1809d43b16e4ae59607882a1c3c50ebee86de8a637508316aa129a5c0cff0222824a62bfc0a8159e527da82c00c0b1a62e73108dd9b481e734f2f3a0c5849b1d4846258d2e1d849ec142c25d4534c30b60c061a2b770fc9d12956763357b7c37e38b9581c" -X POST http://localhost:8080/function/signature
```

Este texto hexadecimal é a assinatura da frase "This is a sample textfile". 

Se quiser usar outro formato, como **JSON**, é muito simples.

Qualquer coisa que você escrever na **stdout** será logado pelo Open FaaS e você poderá obter o log com o comando: 

```
faas-cli logs signature
...
2019-12-13T16:49:09Z 2019/12/13 16:49:09 Metrics listening on port: 8081
2019-12-13T16:49:16Z 2019/12/13 16:49:16 stdout: Parameters: This is a sample textfile:0badcb092f69338ce10f53fb25539fba6ce128751f645fb0bc0180cdd4be0d0ad3eaaffa6ca3510c6044cede112674d90b226eede686592c580cf2d0cb6176118c0eefd5fbf23f3d85e151d516646e483f2aca455f5856b402f50f2e70bf61babc65133da7d9b5576916f9526bb7d419c657e30d61ff3e54212358ee972121e8ec9f9b436c365c7c9930284487bfa457932fefd7dbc3f323252c45b1809d43b16e4ae59607882a1c3c50ebee86de8a637508316aa129a5c0cff0222824a62bfc0a8159e527da82c00c0b1a62e73108dd9b481e734f2f3a0c5849b1d4846258d2e1d849ec142c25d4534c30b60c061a2b770fc9d12956763357b7c37e38b9581c
2019-12-13T16:49:16Z 2019/12/13 16:49:16 stdout: InputStream: sun.net.www.protocol.jar.JarURLConnection$JarURLInputStream@44f88448
2019-12-13T16:49:16Z 2019/12/13 16:49:16 stdout: Return: Signature is true
2019-12-13T16:49:16Z 2019/12/13 16:49:16 POST / - 200 OK - ContentLength: 17
2019-12-13T16:49:16Z 2019/12/13 16:49:16 stdout: Request / 17 bytes written.
```




