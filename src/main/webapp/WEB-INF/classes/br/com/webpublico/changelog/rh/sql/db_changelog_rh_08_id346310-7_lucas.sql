update EVENTOFP
set FORMULA = 'var horas50 = new Array(''145'',''560'',''564'');
var horas100 = new Array(''144'',''561'');

var totalHoras50Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas50));
var totalHoras100Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas100));

var valorBase = calculador.calculaBaseSemRetroativoMesCompleto(''1006'');
var horasMensais = Number(calculador.getEp().getContratoFP().getJornadaDeTrabalho().getHorasSemanal() * 5);
var mesesTrab = Number(calculador.quantidadeMesesTrabalhadosAno());

var valor50 = (valorBase / horasMensais) * 2 * (totalHoras50Ano / mesesTrab);
var valor100 = (valorBase / horasMensais) * 1.5 * (totalHoras50Ano / mesesTrab);

var valor = valor50 + valor100;

return valor;',
    REFERENCIA = 'var horas50 = new Array(''145'',''560'',''564'');
var horas100 = new Array(''144'',''561'');
var totalHoras50Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas50));
var totalHoras100Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas100));
var mesesTrab = Number(calculador.quantidadeMesesTrabalhadosAno());

return (totalHoras50Ano + totalHoras100Ano) / mesesTrab;'
where CODIGO = '270'
