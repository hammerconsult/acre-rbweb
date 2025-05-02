update eventofp
set REGRA                = 'if (calculador.quantidadeMesesTrabalhadosAno() == 0){
 return false;
}
if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return false;
}
if(!calculador.getEp().getContratoFP()){
  return false;
}

return calculador.recuperaTipoFolha() == ''SALARIO_13'' || calculador.recuperaTipoFolha() == ''ADIANTAMENTO_13_SALARIO'' || calculador.recuperaTipoFolha() == ''RESCISAO'';',
    formula              = 'var horas50 = new Array(''145'',''560'');
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
    REFERENCIA           = 'var horas50 = new Array(''145'',''560'');
var horas100 = new Array(''144'',''561'');
var totalHoras50Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas50));
var totalHoras100Ano = Number(calculador.somarReferenciaHorasPagasNoAno(horas100));
var mesesTrab = Number(calculador.quantidadeMesesTrabalhadosAno());

return (totalHoras50Ano + totalHoras100Ano) / mesesTrab;',
    FORMULAVALORINTEGRAL = 'return 0;',
    VALORBASEDECALCULO   = 'return calculador.calculaBaseSemRetroativoMesCompleto(''1006'');'
where codigo = '270'
