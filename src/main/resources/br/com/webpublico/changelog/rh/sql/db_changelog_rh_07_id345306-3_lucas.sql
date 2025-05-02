update EVENTOFP
set FORMULA = 'if(!calculador.optanteFGTS()){
return 0;
}

var aliquota = 8;

if (calculador.obterCategoriaeSocial()  == 103) {
    aliquota = 2;
}

if(calculador.recuperaTipoFolha() == ''ADIANTAMENTO_13_SALARIO''){
return calculador.calculaBase(''1095'') * aliquota / 100 ;
}
return calculador.calculaBase(''1204'') * aliquota / 100 ;',
    FORMULAVALORINTEGRAL = 'var aliquota = 8;

if (calculador.obterCategoriaeSocial()  == 103) {
    aliquota = 2;
}

if(calculador.recuperaTipoFolha() == ''ADIANTAMENTO_13_SALARIO''){
return calculador.calculaBase(''1095'') * aliquota / 100 ;
}
return calculador.calculaBase(''1204'') * aliquota / 100 ;',
    REFERENCIA = 'if (calculador.obterCategoriaeSocial()  == 103) {
    return 2;
}

return 8;'
where CODIGO = '897'
