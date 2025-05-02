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
<td style="width: 50%; text-align: center;"><strong>NOTA DE LIQUIDA&Ccedil;&Atilde;O</strong></td>
<td style="width: 25%;">$NUMERO_LIQUIDACAO</td>
<td style="width: 25%;">$DATA_LIQUIDACAO</td>
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
</td>
<td style="width: 30%; vertical-align: top;">
<div style="text-align: left;"><span>$CPF_CNPJ </span></div>
<div style="text-align: left;">&nbsp;</div>
<div style="border-top: 1px solid; text-align: left;"><span><strong>13. Classe da Pessoa</strong></span></div>
<div style="border-top: 1px solid; text-align: left;"><span>$DESCRICAO_CLASSE_PESSOA </span></div>
</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 70%;"><strong>14. Modalidade</strong></td>
<td style="width: 30%;"><strong>15. Tipo de Empenho</strong></td>
</tr>
<tr>
<td style="width: 70%;">&nbsp;$MODALIDADE_LICITACAO</td>
<td style="width: 30%;">$TIPO_EMPENHO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="text-align: center;"><strong>16. Valor Empenhado R$</strong></td>
<td style="text-align: center;"><strong>17. Valor da Nota R$</strong></td>
<td style="text-align: center;"><strong>18. Saldo a Liquidar R$</strong></td>
</tr>
<tr>
<td>$VALOR_EMPENHADO</td>
<td>$VALOR</td>
<td>$SALDO_ATUAL</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="text-align: left;"><strong>19. Valor por Extenso</strong></td>
</tr>
<tr>
<td>$VALOR_EXTENSO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>22. Hist&oacute;rico</strong></td>
</tr>
<tr>
<td>$HISTORICO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>21.Documentos Comprobat&oacute;rios</strong></td>
</tr>
<tr>
<td>$DOCUMENTOS_COMPROBATORIOS</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td><strong>22.Detalhamento</strong></td>
</tr>
<tr>
<td>$DETALHAMENTOS</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 884px; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="border-right: 0px solid; text-align: left; width: 50%;"><strong>23. Assinaturas</strong></td>
<td style="border-left: 0px solid; text-align: center; width: 50%;">&nbsp;</td>
</tr>
<tr>
<td style="border-right: 0px solid; text-align: center; width: 50%;">
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>______________________________</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</td>
<td style="border-left: 0px solid; text-align: center; width: 50%;">
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>______________________________</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</td>
</tr>
</tbody>
</table>';
BEGIN
update MODELODOCTOOFICIAL set conteudo = texto where TIPODOCTOOFICIAL_ID = (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 145 and tdo.MODULOTIPODOCTOOFICIAL = 'NOTA_LIQUIDACAO');
END;
