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
<td style="width: 50%; text-align: center;"><strong>$NOME_DA_NOTA</strong></td>
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
<td style="width: 70%;"><strong>06. Especifica&ccedil;&atilde;o da A&ccedil;&atilde;o</strong></td>
<td style="width: 30%;"><strong>07. Programa de Trabalho</strong></td>
</tr>
<tr>
<td style="width: 70%;">$DESCRICAO_ACAO</td>
<td style="width: 30%;">$CODIGO_PROGRAMA_TRABALHO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 35%;"><strong>08. Especifica&ccedil;&atilde;o da Despesa</strong></td>
<td style="width: 35%;"><strong>09. Fonte de Recurso | Detalhamento</strong></td>
<td style="width: 30%;"><strong>10. Natureza da Despesa</strong></td>
</tr>
<tr>
<td style="width: 35%;">$ESPECIFICACAO_DESPESA</td>
<td style="width: 35%;">$DESTINACAO_RECURSO</td>
<td style="width: 30%;">$NATUREZA_DESPESA</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 70%;"><strong>11. Pessoa | Endere&ccedil;o</strong></td>
<td style="width: 30%;"><strong>12. CPF/CNPJ</strong></td>
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
<div style="text-align: left;">$CPF_CNPJ</div>
<div style="text-align: left;">&nbsp;</div>
<div style="border-top: 1px solid; text-align: left;"><strong>13. Classe da Pessoa</strong></div>
<div style="border-top: 1px solid; text-align: left;">$DESCRICAO_CLASSE_PESSOA</div>
<div style="text-align: left;">&nbsp;</div>
<div style="border-top: 1px solid; text-align: left;"><strong>14. Banco/Ag&ecirc;ncia/Conta</strong></div>
<div style="border-top: 1px solid; text-align: left;">$BANCO_AGENCIA_CONTA</div>
</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 70%;"><strong>15. Modalidade</strong></td>
<td style="width: 30%;"><strong>16. Tipo de Empenho</strong></td>
</tr>
<tr>
<td style="width: 70%;">&nbsp;$MODALIDADE_LICITACAO</td>
<td style="width: 30%;">$TIPO_EMPENHO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 36px;" border="1" cellspacing="0">
<tbody>
<tr style="height: 18px;">
<td style="text-align: center; height: 18px; width: 297.391px;"><strong>17. Valor Empenhado R$</strong></td>
<td style="text-align: center; height: 18px; width: 266.875px;"><strong>18. Valor da Nota R$</strong></td>
<td style="text-align: center; height: 18px; width: 309.734px;"><strong>19. Saldo a Liquidar R$</strong></td>
</tr>
<tr style="height: 18px;">
<td style="height: 18px; width: 297.391px;">$VALOR_EMPENHADO</td>
<td style="height: 18px; width: 266.875px;">$VALOR</td>
<td style="height: 18px; width: 309.734px;">$SALDO_ATUAL</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="text-align: left;"><strong>20. Valor por Extenso</strong></td>
</tr>
<tr>
<td>$VALOR_EXTENSO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>21. Hist&oacute;rico</strong></td>
</tr>
<tr>
<td>$HISTORICO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>22. Documentos Comprobat&oacute;rios</strong></td>
</tr>
<tr>
<td>$DOCUMENTOS_COMPROBATORIOS</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>23. Detalhamento</strong></td>
</tr>
<tr>
<td>$DETALHAMENTOS</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="border-right: 0px solid; text-align: left; width: 50%;"><strong>24. Assinaturas</strong></td>
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
update MODELODOCTOOFICIAL set conteudo = texto where TIPODOCTOOFICIAL_ID = (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 163 and tdo.MODULOTIPODOCTOOFICIAL = 'NOTA_OBRIGACAO_A_PAGAR');
END;
