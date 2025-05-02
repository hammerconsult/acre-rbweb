update eventofp
set formula = 'return calculador.recuperaQuantificacaoLancamentoTipoReferencia(''919'')  * calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 25H'', ''N I'', ''A'')/100;',
    valorbasedecalculo = 'return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''G 4-A PM SUP'', ''G 4-A PM SUP 25H'', ''N I'', ''A'');'
where codigo = '919'
