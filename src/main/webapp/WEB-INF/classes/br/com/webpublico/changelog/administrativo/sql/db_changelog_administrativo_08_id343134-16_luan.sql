DECLARE
texto CLOB := '<!DOCTYPE html>
<html>
<head>
</head>
<body>
<p style="text-align: center;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
DOCUMENTO DE FORMALIZA&Ccedil;&Atilde;O DE DEMANDA - DFD
</span>
</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
1. INFORMA&Ccedil;&Otilde;ES GERAIS
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
N&uacute;mero do processo administrativo: $NUMERO_PROCESSO&nbsp;
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
Secretaria demandante: $UNIDADE_ADMINISTRATIVA&nbsp;
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
Respons&aacute;vel pelas informa&ccedil;&otilde;es do DFD: $SOLICITANTE&nbsp;
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
A contrata&ccedil;&atilde;o decorrente deste DFD observar&aacute; as regras da $LEI_14133_OU_LEGISLACAO_ANTERIOR&nbsp;</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
2. DESCRI&Ccedil;&Atilde;O DA NECESSIDADE
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$DESCRICAO_NECESSIDADE&nbsp;
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
3. JUSTIFICATIVA DA NECESSIDADE
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$JUSTIFICATIVA
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
4. SOLU&Ccedil;&Atilde;O SUGERIDA PARA CONTRATA&Ccedil;&Atilde;O
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$SOLUCAO_SUGERIDA
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
5. PREVIS&Atilde;O NO PLANO DE CONTRATA&Ccedil;&Otilde;ES ANUAL
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$PREVISAO_PCA
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
6. REQUISITOS PARA A CONTRATA&Ccedil;&Atilde;O DA SOLU&Ccedil;&Atilde;O SUGERIDA
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$DESCRICAO_COTACAO
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
7. ESTIMATIVA DE QUANTIDADES
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$ITENS
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
8. ESTIMATIVA DE VALOR
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
O valor estimado da contrata&ccedil;&atilde;o é de $VALOR_SERVICO ($VALOR_SERVICO_POR_EXTENSO)
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
9. PREVIS&Atilde;O DE IN&Iacute;CIO E DURA&Ccedil;&Atilde;O PRETENDIDOS PARA A CONTRATA&Ccedil;&Atilde;O
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
Data pretendida para o in&iacute;cio do contrato: $INICIO_EXECUCAO&nbsp;
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
Dura&ccedil;&atilde;o pretendida para o contrato: $PRAZO_ENTREGA&nbsp;
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$DESCRICAO_COMPLEMENTAR
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
10. CONTRATA&Ccedil;&Otilde;ES CORRELATAS
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$CONTRATACOES_CORRELATAS
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
11. CONTRATA&Ccedil;&Otilde;ES INTERDEPENDENTES
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$CONTRATACOES_INTERDEPENDENTES
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
12. GRAU DE PRIORIDADE
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$GRAU_PRIORIDADE
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: left;">
<span style="font-weight: bold; font-size: 14pt; font-family: Arial;">
13. APROVA&Ccedil;&Atilde;O
</span>
</p>
<p style="text-align: left;">
<span style="font-size: 12pt; font-family: Arial;">
$APROVADOR_SOLICITACAO
</span>
</p>
<p>&nbsp;</p>
<p style="text-align: center;">
<span style="font-size: 12pt; font-family: Arial;">Rio Branco - AC, $DATA_HOJE_DIA de $DATA_HOJE_MES_EXTENSO de $DATA_HOJE_ANO
</span>
</p>
</body>
</html>';
BEGIN
update MODELODOCTOOFICIAL set conteudo = texto where TIPODOCTOOFICIAL_ID = (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 168 and tdo.MODULOTIPODOCTOOFICIAL = 'DFD');
END;
