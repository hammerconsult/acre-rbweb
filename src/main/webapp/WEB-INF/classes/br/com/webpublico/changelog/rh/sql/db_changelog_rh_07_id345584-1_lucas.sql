update eventofp
set FORMULA = 'var valor1 = calculador.quantidadeValeTransporte() * calculador.obterReferenciaValorFP(''20'').valor;
var valor2 = calculador.calculaBaseSemRetroativo(''1060'') * 6 / 100;

var valorFinal = valor1 > valor2 ? valor2 : valor1;
return valorFinal / calculador.obterDiasDoMes() * (calculador.obterDiasDoMes() - calculador.quantidadeDiasFeriasGozadasNoMes());'
where CODIGO = 681
