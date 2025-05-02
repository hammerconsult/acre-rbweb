update eventofp
set regra = 'return false;',
    formula = 'return calculador.recuperaEvento(''898'', ''BASE'') * calculador.recuperaQuantificacaoLancamentoTipoReferencia(''441'') / 100;',
    formulavalorintegral = 'return 0;',
    valorbasedecalculo = 'return calculador.recuperaEvento(''898'', ''BASE'');'
where codigo = '441'
