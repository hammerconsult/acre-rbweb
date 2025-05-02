update EVENTOFP
set REGRA = 'if (calculador.obterCategoriaeSocial()  == 103) {
    return true;
}

var modalidade = calculador.obterModalidadeContratoFP();
if(modalidade.getCodigo() == 4 &&  calculador.obterCargo(''2208'')){
    return false;
}

if( (modalidade.getCodigo() == 1 || modalidade.getCodigo() == 4 || (calculador.identificaAposentado() && calculador.podeCalcularParaAposentado(''101'')) || (calculador.identificaPensionista() && calculador.podeCalcularParaPensionista(''101'') )) && !calculador.temEventoCategoria(''150'')  ){
    return true;
}
return false;'
where CODIGO = '101'
