update eventofp
set formula = 'var valor_desc_matric_1 = 0;
var valor_base_inss = 0;

Number.prototype.toFixedDown = function (digits) {
    var re = new RegExp("(\\d+\\.\\d{" + digits + "})(\\d)"),
        m = this.toString().match(re);
    return m ? parseFloat(m[1]) : this.valueOf();
};

valor_base_inss = calculador.calculaBase(''1002'');
valor_desc_matric_1 = 0;
if (calculador.recuperaTipoFolha() == ''COMPLEMENTAR'') {
    valor_desc_matric_1 = calculador.valorTotalEventoOutrosVinculos(''900'');
    valor_base_inss = valor_desc_matric_1 > 0 ? calculador.calculaBaseMultiplosVinculos(''1002'') : calculador.calculaBase(''1002'');
}

var Perc_desc = 0;

if (calculador.obterCargo(''2208'') || (calculador.estaNoOrgao(''25'') && calculador.obterModalidadeContratoFP().codigo == 6)) {
    Perc_desc = 11;
} else {
    Perc_desc = calculador.obterReferenciaFaixaFP(''8'', valor_base_inss).percentual;
}

var valor_deduzir = calculador.obterReferenciaFaixaFP(''8'', valor_base_inss).valor;

if (calculador.identificaPensionista()) {
    valor_base_inss = calculador.obterValorPensao();
}


var faixas = [];
var faixa1 = calculador.obterReferenciaFaixaFP(''8'', 1000);
var faixa2 = calculador.obterReferenciaFaixaFP(''8'', 2000);
var faixa3 = calculador.obterReferenciaFaixaFP(''8'', 3000);
var faixa4 = calculador.obterReferenciaFaixaFP(''8'', 6000);

faixas.push(faixa1);
faixas.push(faixa2);
faixas.push(faixa3);
faixas.push(faixa4);

var acumulado = 0;

if (calculador.obterCargo(''2208'')) {
    acumulado = valor_base_inss * (11 / 100);

} else {
    for (var i = 0; i < faixas.length; i++) {
        var faixa = faixas[i];
        var ultimaFaixa = faixas[i - 1];
        if (faixa.referenciaAte <= valor_base_inss) {
            var valorAcumular = (ultimaFaixa ? faixa.referenciaAte - ultimaFaixa.referenciaAte : faixa.referenciaAte) * (faixa.percentual / 100);

            acumulado = acumulado + valorAcumular.toFixedDown(2);
        } else {
            var valorAcumular = (ultimaFaixa ? valor_base_inss - ultimaFaixa.referenciaAte : valor_base_inss) * (faixa.percentual / 100);

            acumulado = acumulado + valorAcumular;

            break;
        }
    }

}


var desc_inss = acumulado;

var teto = calculador.obterReferenciaValorFP(''18'').valor;

if (calculador.identificaPensionista()) {
    println(''desc_inss identificaPensionista '' + desc_inss);
    desc_inss = desc_inss / calculador.obterNumeroCotas();
}

if (parseFloat(desc_inss).toFixed(2) >= teto && valor_desc_matric_1 <= 0) {

    return teto;
} else if (desc_inss <= teto && valor_desc_matric_1 <= 0) {

    return desc_inss;
} else if (desc_inss >= teto && valor_desc_matric_1 > 0) {

    return teto - valor_desc_matric_1;
} else if (desc_inss < teto && valor_desc_matric_1 > 0) {

    return desc_inss - valor_desc_matric_1;
}
'
where codigo = '900'
