update eventofp
set formula = 'return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''RBTRANS G 2 AO EM'', ''RBTRANS G 2 AO EM'', ''N I'', ''A'') * calculador.recuperaQuantificacaoLancamentoTipoReferencia(''461'') / 100;',
    formulavalorintegral = 'return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''RBTRANS G 2 AO EM'', ''RBTRANS G 2 AO EM'', ''N I'', ''A'') * calculador.recuperaQuantificacaoLancamentoTipoReferencia(''461'') / 100;',
    valorbasedecalculo = 'return calculador.enquadramentoPCCR(''REESTRUTURACAO PCCR 2022'', ''RBTRANS G 2 AO EM'', ''RBTRANS G 2 AO EM'', ''N I'', ''A'');'
where codigo = '461'
