DECLARE
texto CLOB := '<table style="border-collapse: collapse; width: 100%; height: 40px;" border="0">
<tbody>
<tr>
<td style="width: 100%;">
<div style="text-align: left; font-family: arial, helvetica, sans-serif;"><span style="font-size: 12pt; font-family: arial, helvetica, sans-serif;"><strong>Estado do Acre</strong></span></div>
<div style="text-align: left; font-family: arial, helvetica, sans-serif;"><span style="font-size: 12pt; font-family: arial, helvetica, sans-serif;"><strong>Municipio de Rio Branco</strong></span></div>
</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 100%; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 50%;"><strong>Recibo</strong></td>
<td style="width: 25%;"><strong>Tipo</strong></td>
<td style="width: 25%;"><strong>Data Registro</strong></td>
</tr>
<tr>
<td style="width: 50%; text-align: center;"><strong>$RECIBO</strong></td>
<td style="width: 25%;">$TIPO</td>
<td style="width: 25%;">$DATA_REGISTRO</td>
</tr>
</tbody>
</table>
<table style="border-collapse: collapse; width: 100%; height: 40px;" border="1" cellspacing="0">
<tbody>
<tr>
<td style="width: 30%;"><strong>C&oacute;digo/Descri&ccedil;&atilde;o Resposta</strong></td>
<td style="width: 10%;"><strong>M&ecirc;s/Ano</strong></td>
<td style="width: 20%;"><strong>Empresa</strong></td>
<td style="width: 20%;"><strong>Situa&ccedil;&atilde;o</strong></td>
<td style="width: 20%;"><strong>Opera&ccedil;&atilde;o</strong></td>
</tr>
<tr>
<td style="width: 30%;">$CODIGO_RESPOSTA - $DESCRICAO_RESPOSTA</td>
<td style="width: 10%;">$MES_ANO</td>
<td style="width: 20%;">$EMPRESA</td>
<td style="width: 20%;">$SITUACAO</td>
<td style="width: 20%;">$OPERACAO</td>
</tr>
</tbody>
</table>';
BEGIN
update MODELODOCTOOFICIAL set conteudo = texto where TIPODOCTOOFICIAL_ID = (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 160 and tdo.MODULOTIPODOCTOOFICIAL = 'RECIBO_REINF');
END;
