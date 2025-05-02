update EVENTOFP
set FORMULA = 'var base = prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002'');

var desconto = prestador.valorTotalEventoOutrosVinculos(''4080'');

var valorBruto = base * (prestador.avaliaReferencia(''4080'') / 100);
var valorFinal = valorBruto - desconto;

if (valorFinal < 0) {
return 0.0;
} else {
return valorFinal;
}'
where CODIGO = 4080
