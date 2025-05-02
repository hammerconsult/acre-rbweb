update eventofp
set FORMULA              = replace(FORMULA, 'if (calculador.identificaPensionista() && calculador.podeCalcularParaPensionista(''600'')) {', 'if (calculador.identificaPensionista()) {'),
    FORMULAVALORINTEGRAL = 'return 0;',
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, 'if (calculador.identificaPensionista() && calculador.podeCalcularParaPensionista(''600'')) {', 'if (calculador.identificaPensionista()) {')
where codigo = '1100';

update eventofp
set FORMULA              = replace(FORMULA, 'return calculador.obterValorPensionista(''600'');', 'return calculador.obterValorPensionista(''CALCULO'', ''TODAS'');'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, 'return calculador.obterValorPensionista(''600'');', 'return calculador.obterValorPensionista(''CALCULO'', ''TODAS'');')
where codigo = '1100';
