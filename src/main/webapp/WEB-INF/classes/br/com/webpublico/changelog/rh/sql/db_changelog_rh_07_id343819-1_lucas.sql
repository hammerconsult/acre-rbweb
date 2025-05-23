update EVENTOFP
set FORMULA              = 'var baseFGTS = calculador.calculaBase(''1019'');

if(baseFGTS < 0){
 return 0;
}

return baseFGTS * calculador.avaliaReferencia(''909'') / 100;',
    FORMULAVALORINTEGRAL = 'return 0;',
    REFERENCIA           = 'return calculador.obterReferenciaValorFP(''24'').valor;',
    VALORBASEDECALCULO   = 'var baseFGTS = calculador.calculaBase(''1019'');

if(baseFGTS < 0){
 return 0;
}

return baseFGTS;'
where CODIGO = 909
