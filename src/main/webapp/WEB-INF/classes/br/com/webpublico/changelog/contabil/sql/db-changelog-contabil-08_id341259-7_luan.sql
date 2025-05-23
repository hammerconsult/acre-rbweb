DECLARE
texto CLOB := '<table style="border-collapse: collapse; width: 884px; height: 40px;" border="0">
<tbody>
<tr>
<td style="width: 100%;">
<div style="text-align: left; font-family: arial, helvetica, sans-serif;"><span style="font-size: 12pt; font-family: arial, helvetica, sans-serif;"><strong>Estado do Acre</strong></span></div>
<div style="text-align: left; font-family: arial, helvetica, sans-serif;"><span style="font-size: 12pt; font-family: arial, helvetica, sans-serif;"><strong>Municipio de Rio Branco</strong></span></div>
</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 50%;"><strong>01. Documento</strong></td>
<td style="width: 25%;"><strong>02. N&uacute;mero</strong></td>
<td style="width: 25%;"><strong>03. Data</strong></td>
</tr>
<tr>
<td style="width: 50%; text-align: center;"><strong>NOTA DE ESTORNO DE RECEITA EXTRAOR&Ccedil;AMENT&Aacute;RIA<br /></strong></td>
<td style="width: 25%;">$NUMERO_DOCUMENTO</td>
<td style="width: 25%;">$DATA_DOCUMENTO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 75%;"><strong>04. &Oacute;rg&atilde;o</strong></td>
<td style="width: 25%;"><strong>04.a C&oacute;digo</strong></td>
</tr>
<tr>
<td style="width: 75%;">$DESCRICAO_ORGAO</td>
<td style="width: 25%;">$CODIGO_ORGAO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 75%;"><strong>05. Unidade Or&ccedil;ament&aacute;ria</strong></td>
<td style="width: 25%;"><strong>05.a C&oacute;digo</strong></td>
</tr>
<tr>
<td style="width: 75%;">$DESCRICAO_UNIDADE</td>
<td style="width: 25%;">$CODIGO_UNIDADE</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 70%;"><strong>06. Conta Extraor&ccedil;ament&aacute;ria</strong></td>
<td style="width: 30%;"><strong>07. Fonte de Recurso | Detalhamento<br /></strong></td>
</tr>
<tr>
<td style="width: 70%;">$CONTA_EXTRAORCAMENTARIA</td>
<td style="width: 30%;">$DESTINACAO_RECURSO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 70%;"><strong>08. Pessoa | Endere&ccedil;o</strong></td>
<td style="width: 30%;"><strong>09. CPF/CNPJ</strong></td>
</tr>
<tr>
<td style="width: 70%;">
<p>Nome:$NOME_PESSOA</p>
<p>Logradouro:$LOGRADOURO</p>
<p>Bairro:$BAIRRO CEP:$CEP</p>
<p>Cidade:$CIDADE &nbsp;&nbsp;U.F:$UF</p>
<p>Banco:&nbsp;$DESCRICAO_BANCO</p>
<p>Ag&ecirc;ncia:&nbsp;$DESCRICAO_AGENCIA</p>
</td>
<td style="width: 30%; vertical-align: top;">
<div style="text-align: left;"><span>$CPF_CNPJ </span></div>
<div style="text-align: left;">&nbsp;</div>
<div style="border-top: 1px solid; text-align: left;"><span><strong>10. Classe da Pessoa</strong></span></div>
<div style="border-top: 1px solid; text-align: left;"><span>$DESCRICAO_CLASSE_PESSOA </span></div>
<div style="text-align: left;">&nbsp;</div>
<div style="border-top: 1px solid; text-align: left;"><span><strong>11. Banco/Ag&ecirc;ncia/Conta</strong></span></div>
<div style="border-top: 1px solid; text-align: left;"><span>$BANCO_AGENCIA_CONTA </span></div>
</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="text-align: center;"><strong>12. Saldo Anterior R$</strong></td>
<td style="text-align: center;"><strong>13. Valor da Nota R$</strong></td>
<td style="text-align: center;"><strong>14. Saldo Atual R$</strong></td>
</tr>
<tr>
<td>$SALDO_ANTERIOR</td>
<td>$VALOR</td>
<td>$SALDO_ATUAL</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="text-align: left;"><strong>15. Valor por Extenso</strong></td>
</tr>
<tr>
<td>$VALOR_EXTENSO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>16. Hist&oacute;rico</strong></td>
</tr>
<tr>
<td>$HISTORICO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="border-right: 0px solid; text-align: left; width: 50%;"><strong>17. Assinaturas</strong></td>
<td style="border-left: 0px solid; text-align: center; width: 50%;">&nbsp;</td>
</tr>
<tr>
<td style="border-right: 0px solid; text-align: center; width: 50%;">
<p>&nbsp;&nbsp;</p>
<p>______________________________</p>
<p>&nbsp;&nbsp;</p>
</td>
<td style="border-left: 0px solid; text-align: center; width: 50%;">
<p>&nbsp;&nbsp;</p>
<p>______________________________</p>
<p>&nbsp;&nbsp;</p>
</td>
</tr>
</tbody>
</table>';
BEGIN
update MODELODOCTOOFICIAL set conteudo = texto where TIPODOCTOOFICIAL_ID = (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 153 and tdo.MODULOTIPODOCTOOFICIAL = 'NOTA_RECEITA_EXTRA_ESTORNO');
END;
