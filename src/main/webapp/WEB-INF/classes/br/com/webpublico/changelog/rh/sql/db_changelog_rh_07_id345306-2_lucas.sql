update EVENTOFP
set FORMULA = 'var aliquota = 8;

if (calculador.obterCategoriaeSocial()  == 103) {
    aliquota = 2;
}

return calculador.calculaBase(''1005'') * aliquota / 100 ;',
    REFERENCIA = 'if (calculador.obterCategoriaeSocial()  == 103) {
    return 2;
}

return 8;'
where CODIGO = '904'
