update eventofp
set VALORBASEDECALCULO = 'var valorAposentadoria  = calculador.obterValorAposentadoria([''101'',''363'',''369'',  ''444'']);

if(calculador.identificaAposentado() ){
	var base = 0;
	if(valorAposentadoria > 0 ) {
	base =  valorAposentadoria;
	} else {
	base = calculador.salarioBase();
	}
    return base;
}

var valor = (calculador.salarioBase() / calculador.obterDiasDoMes()) * calculador.diasTrabalhados();
valor = parseFloat(valor).toFixed(2);
return valor;'
where codigo = '103'
