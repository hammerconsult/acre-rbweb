update eventofp
set formula = replace(replace(replace(replace(FORMULA, 'base = calculador.identificaPensionista() ? (base * calculador.obterNumeroCotas()): base;', ' '), 'if(base < calculador.obterValorPensionista()){', 'if(base < calculador.calculaBaseInstituidorPensaoPrev()){'), 'base = calculador.obterValorPensionista();', 'base = calculador.calculaBaseInstituidorPensaoPrev();'), 'calc = calc / calculador.obterNumeroCotas();', 'calc = (base * (refe / 100) * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());'),
    VALORBASEDECALCULO   = replace(replace(replace(VALORBASEDECALCULO, 'base = calculador.identificaPensionista() ? (base * calculador.obterNumeroCotas()): base;', ' '),'base = calculador.obterValorPensionista();', 'base = calculador.calculaBaseInstituidorPensaoPrev();'),'base = base;', 'base = base;
    if (calculador.identificaPensionista()){
        return Number(base * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
    } ')
where codigo = '898'
