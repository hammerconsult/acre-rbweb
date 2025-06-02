# WEBPUBLICO - ERP

______
## Requisitos para executar o branch *master*

- Java 8
- Wildlfy 15.0.1.Final

Existe uma copia de um servidor de a aplicação configurada na raiz do FTP, chamada:  wildfly-15.0.1.Final-RB.zip

Há também uma versão do webserver compartilhado no link:

https://drive.google.com/drive/folders/10qlNg0MGis2ySzsnvssDVLj20A3Jw09n?usp=sharing

OBS: Deve ser solicitado o acesso

#### Descompactar a pasta e anexar o novo Wildfly no Intellij.

#### Trocar a JDK para a Open JDK 8 ou Oracle JDK 8.

______________________________________

## REDIS - Docker
sudo docker run --name redis -d -p 6379:6379 redis redis-server --requirepass "senha10"

Configuração: RedisService
Url padrão: redis://:senha10@localhost:6379

### Redis
Conexão com o ambiente de homologação
`sudo redis-cli -h 172.16.0.73 -p 6379 -a senha10`


______________________________________
### Instalação Kubernetes
Antes de realizar a instalação:
1. deve ser desativada a swap da vm.
2. Deve comentar a linha /etc/containerd/config.toml
   #disabled_plugins = ["cri"]

Passos para a instalação:
1. Seguir a instalação comum dos pacotes kubeadm, kubectl e kublet seguindo o tutorial:

    https://kubernetes.io/pt-br/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

2. Inicializar o control plane com o comando
   `kubeadm init --pod-network-cidr=10.244.0.0/16`

3. Rodar os comandos apresentados em tela após a instalação do cluster.
4. Instalar a network addon do cluster
   `kubectl apply -f https://github.com/flannel-io/flannel/releases/latest/download/kube-flannel.yml`

5. Instalar os pacotes de instalação no passo 1 em cada worker node.

6. Rodar o comando kubeadm join em cada worker node.

Para verificar se sua instalação foi realizada com sucesso, rode o comando `kubectl get nodes`, e veja se o status dos
nodes estão como 'ready'

_________________________

### Integração via API Rest

1. As API Rest possuem segurança via API Key, para consumir uma API Rest deverá informar a API Key
   no header da requisição: "API-KEY": "<API-KEY-GERADA-WEBPUBLICO>"
2. A geração da API Key pode ser feita na tela /comum/api-key/novo/

### Integração com a Softplan (Saj procuradorias)

1. Para gerar as classes a partir do wsdl:
 - Execute o comando: <Path JDK8/bin>./wsimport -d <path de saída> -p br.com.webpublico.negocios.tributario.softplan.ws -keep -verbose <URL WSDL>;
 - Copie os arquivos .java para o pacote br.com.webpublico.negocios.tributario.softplan.ws do projeto;

2. Para testar um dos serviços via SOAPUI:
 - File>New SOAP Project em Initial WSDL cole a <URL WSDL>;
 - Execute o serviço "logon" passando o <NOME USUARIO> e <SENHA>, copie o valor do header "set-cookie" da resposta do serviço.
 - Para executar qualquer outro serviço, no request adicione na aba "Headers" o header "Cookie" com o valor copiado anteriormente;

Obs: As informações <URL WSDL>, <NOME USUARIO> e <SENHA> estão configurados na tabela configuracaowebservice, faça o filtro pelo tipo="PROCURADORIA",
ou abra <URL WEBPUBLICO>/configuracao-tributario/listar/ na aba "WebServices".



