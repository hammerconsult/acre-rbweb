update eventofp
set regra                = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return false;
}
if(!calculador.getEp().getContratoFP()){
  return false;
}

return calculador.recuperaTipoFolha() == ''NORMAL'' || calculador.recuperaTipoFolha() == ''RESCISAO'';',
    FORMULA              = 'var eventosHoras50 = new Array(''145'',''560'');
var eventosHoras100 = new Array(''144'',''561'');
var tipoFolhas = new Array(''NORMAL'',''COMPLEMENTAR'',''MANUAL'');
var valorBase = calculador.calculaBaseSemRetroativoMesCompleto(''1077'');
var horasMensais = Number(calculador.getEp().getContratoFP().getJornadaDeTrabalho().getHorasSemanal() * 5);
var duracaoBasePA = calculador.duracaoBasePeriodoAquisitivo(''FERIAS'');


//Férias Concedidas
var totalHoras50FerCon = calculador.buscarTotalEventos(''FERIAS'', ''REFERENCIA'', tipoFolhas, eventosHoras50, false, false, false);
var totalHoras100FerCon = calculador.buscarTotalEventos(''FERIAS'', ''REFERENCIA'', tipoFolhas, eventosHoras100, false, false, false);
var valor50FerCon = (valorBase / horasMensais) * 2 * (totalHoras50FerCon / duracaoBasePA);
var valor100FerCon = (valorBase / horasMensais) * 1.5 * (totalHoras100FerCon / duracaoBasePA);

var valorFerCon = valor50FerCon + valor100FerCon;


//Férias Proporcionais
var totalHoras50FerPro = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASPROP'', ''REFERENCIA'', tipoFolhas, eventosHoras50, false, false, false) : 0;
var totalHoras100FerPro = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASPROP'', ''REFERENCIA'', tipoFolhas, eventosHoras100, false, false, false) : 0;
var valor50FerPro = (valorBase / horasMensais) * 2 * (totalHoras50FerPro / duracaoBasePA);
var valor100FerPro = (valorBase / horasMensais) * 1.5 * (totalHoras100FerPro / duracaoBasePA);

var valorFerPro = valor50FerPro + valor100FerPro;


//Férias Vencidas
var totalHoras50FerVen = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASVENC'', ''REFERENCIA'', tipoFolhas, eventosHoras50, false, false, false) : 0;
var totalHoras100FerVen = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASVENC'', ''REFERENCIA'', tipoFolhas, eventosHoras100, false, false, false) : 0;
var valor50FerVen = (valorBase / horasMensais) * 2 * (totalHoras50FerVen / duracaoBasePA);
var valor100FerVen = (valorBase / horasMensais) * 1.5 * (totalHoras100FerVen / duracaoBasePA);

var valorFerVen = valor50FerVen + valor100FerVen;


//Férias Dobradas
var totalHoras50FerDob = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASDOBR'', ''REFERENCIA'', tipoFolhas, eventosHoras50, false, false, false) : 0;
var totalHoras100FerDob = calculador.recuperaTipoFolha() == ''RESCISAO'' ? calculador.buscarTotalEventos(''FERIASDOBR'', ''REFERENCIA'', tipoFolhas, eventosHoras100, false, false, false) : 0;
var valor50FerDob = (valorBase / horasMensais) * 2 * (totalHoras50FerDob / duracaoBasePA);
var valor100FerDob = (valorBase / horasMensais) * 1.5 * (totalHoras100FerDob / duracaoBasePA);

var valorFerDob = valor50FerDob + valor100FerDob;

var valorFinal = valorFerCon + valorFerPro + valorFerVen + valorFerDob;

return valorFinal;',
    FORMULAVALORINTEGRAL = 'return 0;',
    REFERENCIA           = 'return 0;',
    VALORBASEDECALCULO   = 'return calculador.calculaBaseSemRetroativo(''1077'');'

where codigo = '268'
