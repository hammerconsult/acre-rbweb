update EVENTOFP
set REFERENCIA = 'if (calculador.obterCategoriaeSocial()  == 103) {
    return 2;
}
return calculador.obterReferenciaValorFP(''24'').valor;'
where CODIGO = '909'
