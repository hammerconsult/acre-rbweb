update eventofp
    set formula = replace(formula,'calculador.obterValorPensao()'
        ,'calculador.obterValorPensionista()')
    where codigo = '898';
update eventofp
    set VALORBASEDECALCULO = replace(VALORBASEDECALCULO,'calculador.obterValorPensao()'
        ,'calculador.obterValorPensionista()')
    where codigo = '898';
