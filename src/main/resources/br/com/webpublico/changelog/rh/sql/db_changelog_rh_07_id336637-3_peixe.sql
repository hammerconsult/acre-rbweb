update eventofp
set referencia = 'var valor_base_inss = calculador.calculaBase(''1002'');

if (calculador.recuperaTipoFolha() == ''COMPLEMENTAR'') {
    var valor_desc_matric_1 = calculador.valorTotalEventoOutrosVinculos(''900'');
    valor_base_inss = valor_desc_matric_1 > 0 ? calculador.calculaBaseMultiplosVinculos(''1002'') : calculador.calculaBase(''1002'');
}

var Perc_desc = 0;

if (calculador.obterCargo(''2208'') || (calculador.estaNoOrgao(''25'') && calculador.obterModalidadeContratoFP().codigo == 6)){
    Perc_desc = 11;
} else {
    Perc_desc = calculador.obterReferenciaFaixaFP(''8'', valor_base_inss).percentual;
}
return Perc_desc;'
where codigo = '900'
