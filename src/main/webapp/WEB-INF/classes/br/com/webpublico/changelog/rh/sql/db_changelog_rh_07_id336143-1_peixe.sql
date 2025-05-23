update eventofp
set REGRA                = 'if(calculador.quantidadeMesesTrabalhadosAno() == 0 || calculador.quantidadeMesesTrabalhadosAno() >= 12){
    return false;
}
return (calculador.identificaPensionista() || calculador.identificaAposentado()) && calculador.getEp().getMes() == 12;
',
    FORMULA              = 'return (calculador.calculaBaseSemRetroativo(''1053'') / 12)  * calculador.quantidadeMesesTrabalhadosAno();',
    FORMULAVALORINTEGRAL = 'return (calculador.calculaBaseSemRetroativo(''1053'') / 12)  * calculador.quantidadeMesesTrabalhadosAno();',
    REFERENCIA           = 'return calculador.quantidadeMesesTrabalhadosAno();',
    VALORBASEDECALCULO   = 'return calculador.calculaBaseSemRetroativo(''1053'');',
    AUTOMATICO           = 1,
    CALCULORETROATIVO    = 1
where CODIGO = '918'
