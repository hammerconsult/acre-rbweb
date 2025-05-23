update eventofp
set formula = 'var progressao = calculador.progressaoServidor();
var enquadramentoPCCR = 0;

if (progressao.trim() == ''G 4-A PM SUP 25H'') {
    enquadramentoPCCR = calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 25H'', ''N I'', ''A'');
} else if (progressao.trim() == ''G 4-A PM SUP 40H'') {
    enquadramentoPCCR = calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 40H'', ''N I'', ''A'');
}

return calculador.recuperaQuantificacaoLancamentoTipoReferencia(''919'') * enquadramentoPCCR / 100;',
    valorbasedecalculo = 'var progressao = calculador.progressaoServidor();

if (progressao.trim() == ''G 4-A PM SUP 25H'') {
    return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 25H'', ''N I'', ''A'');
}

if (progressao.trim() == ''G 4-A PM SUP 40H'') {
    return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 40H'', ''N I'', ''A'');
}

return 0;'
where codigo = '919'
