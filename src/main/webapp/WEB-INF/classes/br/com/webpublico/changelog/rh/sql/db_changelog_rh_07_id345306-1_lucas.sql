update EVENTOFP
set REGRA = 'if (calculador.obterCategoriaeSocial()  == 103) {
    return false;
}

var modalidade = calculador.obterModalidadeContratoFP();
if(modalidade.getCodigo() == 1 || modalidade.getCodigo() == 4 || calculador.identificaAposentado()){

    var valor = calculador.obterReferenciaValorFP(''5'').valor > calculador.salarioBase();

    var codigo = calculador.obterTipoPrevidenciaFP();

var valorAposentadoria = 0;
    var valor363 = calculador.avaliaEvento(''363'');
    if(valor363 > 0){
        valorAposentadoria += Number(valor363);
        valorAposentadoria += Number(calculador.obterValorAposentadoria([''101'', ''122'', ''369'', ''444'']));
    }else {
        valorAposentadoria += Number(calculador.obterValorAposentadoria([''101'', ''122'', ''363'', ''369'', ''444'']));
    }

    if (calculador.identificaAposentado()) {
        var base = 0;
        if (valorAposentadoria > 0) {
            base = valorAposentadoria;
        } else {
            base = calculador.salarioBase();
        }

        var val = calculador.obterReferenciaValorFP(''5'').valor - base;
        if ((codigo == ''3'' || codigo == ''1'') && (val > 0)) {
            return true;
        } else {
            return false;
        }
    }

    if ((codigo == ''3'' || codigo == ''1'') && (valor > 0)) {
        return true;
    } else {
        return false;
    }
}
return false;'
where CODIGO = '103'
