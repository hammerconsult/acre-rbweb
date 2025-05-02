INSERT INTO CONFIGURACAONFSE VALUES (hibernate_sequence.nextval, NULL, NULL);
INSERT INTO CONFIGURACAOGERALNFSE VALUES (hibernate_sequence.nextval, 0);
INSERT INTO CONFIGURACAOEMAILNFSE VALUES (hibernate_sequence.nextval, null, null, null, null, null);
UPDATE CONFIGURACAONFSE SET CONFIGURACAOEMAILNFSE_ID = (SELECT ID FROM CONFIGURACAOEMAILNFSE);
UPDATE CONFIGURACAONFSE SET CONFIGURACAOGERALNFSE_ID = (SELECT ID FROM CONFIGURACAOGERALNFSE);

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'FALE_CONOSCO','<pre>Esta mensagem é uma solicitação de contato enviada pelo Fale Conosco do Sistema WP ISS Emissão de NFS-e

Dados do Solicitante:

Nome Completo: $FALECONOSCO_NOMECOMPLETO
CPF/CNPJ: $FALECONOSCO_CPFCNPJ
Telefone: $FALECONOSCO_TELEFONE
E-mail: $fALECONOSCO_EMAIL
Data e Hora do Envio: $FALECONOSCO_DATAHORAENVIO

Mensagem:

$FALECONOSCO_MENSAGEM

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO </pre>', (select id from CONFIGURACAONFSE));



insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'ATIVACAO_CADASTRO','<pre>Prezado $NOMEUSUARIO,

Você realizou seu cadastro no sistema WP ISS Emissão de NFS-e.

Por gentileza, clique no endereço abaixo para que seu cadastro seja ativado.

----------------------------------------------------------------------------------
$LINK_ATIVACAO_CADASTRO
----------------------------------------------------------------------------------

Mensagem enviada automaticamente. Por favor, não responda.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'SOLICITACAO_ALTERACAO_SENHA','<pre>Prezado $NOMEUSUARIO,

Você solicitou alteração de sua senha de acesso ao sistema WP ISS Emissão de NFS-e.

Por gentileza, clique no endereço abaixo e informe nova senha:

----------------------------------------------------------------------------------
$LINK_ALTERACAO_SENHA
----------------------------------------------------------------------------------

Mensagem enviada automaticamente. Por favor, não responda.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'NFSE_EMITIDA_TOMADOR','<pre>Prezado Senhor(a),

Esta mensagem refere-se à Nota Fiscal de Serviços Eletrônica Nº $NFSE_NUMERO, emitida pelo Prestador de Serviços:

Razão Social: $PRESTADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $PRESTADOR_INSCRICAO_MUNICIPAL
CPF/CNPJ: $PRESTADOR_CPF_CNPJ

Ao Tomador de Serviços:

Razão Social: $TOMADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $TOMADOR_CMM
CPF/CNPJ: $TOMADOR_CPF_CNPJ

Para visualizá-la acesse o link a seguir:

---------------------------------------------------------------------------
$NFSE_LINK
---------------------------------------------------------------------------

Alternativamente, acesse o portal http://xxxxxxxx.com.br/consultanfs-e
e verifique a autenticidade desta NFS-e informando os dados a seguir:

CPF/CNPJ do Prestador de Serviços: $PRESTADOR_CPF_CNPJ
Número da NFS-e: $NFSE_NUMERO
Código de Verificação: $NFSE_CODIGO_VERIFICACAO

Mensagem enviada automaticamente pelo sistema WP ISS Emissão de NFS-e. Por favor, não responda este e-mail.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'NFSE_EMITIDA_CONTADOR','<pre>Prezado Contador(a),

Esta mensagem refere-se à Nota Fiscal de Serviços Eletrônica Nº $NFSE_NUMERO, emitida pelo Prestador de Serviços:

Razão Social: $PRESTADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $PRESTADOR_CMC
CPF/CNPJ: $PRESTADOR_CPF_CNPJ

Ao Tomador de Serviços:

Razão Social: $TOMADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $TOMADOR_CMC
CPF/CNPJ: $TOMADOR_CPF_CNPJ

Para visualizá-la acesse o link a seguir:

---------------------------------------------------------------------------
[#NFSE_LINK]
---------------------------------------------------------------------------

Alternativamente, acesse o portal http://xxxxxxxx.com.br/consultanfs-e
e verifique a autenticidade desta NFS-e informando os dados a seguir:

CPF/CNPJ do Prestador de Serviços: $PRESTADOR_CPF_CNPJ
Número da NFS-e: $NFSE_NUMERO
Código de Verificação: $NFSE_CODIGO_VERIFICACAO

Mensagem enviada automaticamente pelo sistema de WP ISS Emissão de NFS-e. Por favor, não responda este e-mail.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'NFSE_CANCELADA_TOMADOR','<pre>Prezado Senhor(a),

Informamos que a Nota Fiscal de Serviços Eletrônica abaixo mencionada foi CANCELADA:

Nº da NFS-e: $NFSE_NUMERO
Razão Social do Prestador de Serviços: $PRESTADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $PRESTADOR_CMC
CPF/CNPJ: $PRESTADOR_CPF_CNPJ

Tomador de Serviços:

Razão Social: $TOMADOR_RAZAO_SOCIAL
Cadastro Mobiliário Municipal: $TOMADOR_CMC
CPF/CNPJ: $TOMADOR_CPF_CNPJ

Para visualizá-la acesse o link a seguir:

---------------------------------------------------------------------------
$NFSE_LINK
---------------------------------------------------------------------------

Alternativamente, acesse o portal http://xxxxxxxx.com.br/consultanfs-e
e verifique a autenticidade desta NFS-e informando os dados a seguir:

CPF/CNPJ do Prestador de Serviços: $PRESTADOR_CPF_CNPJ
Número da NFS-e: $NFSE_NUMERO
Código de Verificação: $NFSE_CODIGO_VERIFICACAO

Mensagem enviada automaticamente pelo sistema WP ISS Emissão de NFS-e. Por favor, não responda este e-mail.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));

insert into CONFIGURACAOTEMPLATENFSE values (hibernate_sequence.nextval, 'NFSE_CANCELADA_CONTADOR','<pre>Prezado Contador(a),,

Informamos que a Nota Fiscal de Serviços Eletrônica abaixo mencionada foi CANCELADA:

Nº da NFS-e: [#NFSE_NUMERO]
Razão Social do Prestador de Serviços: [#PRESTADOR_RAZAOSOCIAL]
Cadastro Mobiliário Municipal: [#PRESTADOR_CMC]
CPF/CNPJ: [#PRESTADOR_CPFCNPJ]

Tomador de Serviços:

Razão Social: [#TOMADOR_RAZAOSOCIAL]
Cadastro Mobiliário Municipal: [#TOMADOR_CMC]
CPF/CNPJ: [#TOMADOR_CPFCNPJ]

Para visualizá-la acesse o link a seguir:

---------------------------------------------------------------------------
[#NFSE_LINK]
---------------------------------------------------------------------------

Alternativamente, acesse o portal http://xxxxxxxx.com.br/consultanfs-e
e verifique a autenticidade desta NFS-e informando os dados a seguir:

CPF/CNPJ do Prestador de Serviços: [#PRESTADOR_CPFCNPJ]
Número da NFS-e: [#NFSE_NUMERO]
Código de Verificação: [#NFSE_CODIGOVERIFICACAO]

Mensagem enviada automaticamente pelo sistema WP ISS Emissão de NFS-e. Por favor, não responda este e-mail.

Atenciosamente,

SECRETARIA DE FAZENDA

PREFEITURA MUNICIPAL DE CIDADE MODELO</pre>', (select id from CONFIGURACAONFSE));
