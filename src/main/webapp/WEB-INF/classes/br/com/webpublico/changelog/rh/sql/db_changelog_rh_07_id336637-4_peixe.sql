update eventofp
set VALORBASEDECALCULO = 'if (calculador.recuperaTipoFolha() == ''COMPLEMENTAR'') {
    var valor_desc_matric_1 = calculador.valorTotalEventoOutrosVinculos(''900'');
    var valor_base_inss = valor_desc_matric_1 > 0 ? calculador.calculaBaseMultiplosVinculos(''1002'') : calculador.calculaBase(''1002'');
    return valor_base_inss;
}
return calculador.calculaBase(''1002'');
'
where codigo = '900'
