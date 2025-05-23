update eventofp
set formula = replace(replace(replace(replace(replace(replace(FORMULA, 'base = calculador.identificaPensionista() ? (base * calculador.obterNumeroCotas()): base;', ' '), 'if(base < calculador.obterValorPensionista()){', 'if(base < calculador.calculaBaseInstituidorPensaoPrev()){'), 'base = calculador.obterValorPensionista();', 'base = calculador.calculaBaseInstituidorPensaoPrev();'), 'calc = calc / calculador.obterNumeroCotas();', 'calc = (base * (refe / 100) * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());'), 'if(base < calculador.obterValorPensao()){', 'if(base < calculador.calculaBaseInstituidorPensaoPrev()){'), 'base = calculador.obterValorPensao();', 'base = calculador.calculaBaseInstituidorPensaoPrev();'),
    VALORBASEDECALCULO   = replace(replace(replace(VALORBASEDECALCULO, 'base = calculador.obterValorPensionista();', 'base = calculador.calculaBaseInstituidorPensaoPrev();'),'base = calculador.identificaPensionista() ? (base * calculador.obterNumeroCotas()): base;', ' '), 'base = base;', 'base = base;
    if (calculador.identificaPensionista()){
        return Number(base * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
    }')
where codigo = '896'
