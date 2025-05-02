update eventofp
set regra                   = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return false;
}
if(!calculador.getEp().getContratoFP()){
  return false;
}
if(!calculador.estaNoOrgao(''24'')){
  return false;
}
return calculador.totalHorasExtras(50) > 0;',
    AUTOMATICO              = 1,
    ORDEMPROCESSAMENTO      = 564,
    TIPOBASEFP_ID           = 133838375,
    PROPORCIONALIZADIASTRAB = 0,
    UNIDADEREFERENCIA       = 'Hrs',
    TIPOLANCAMENTOFP        = 'HORAS',
    FORMULA                 = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return 0;
}
if(!calculador.getEp().getContratoFP()){
  return 0;
}
var lanc_he_50 = calculador.totalHorasExtras(50);
return calculador.calculaBaseSemRetroativoMesCompleto(''1031'') / (calculador.getEp().getContratoFP().getJornadaDeTrabalho().getHorasSemanal() * 5)  * 1.5 *  calculador.totalHorasExtras(50);',
    FORMULAVALORINTEGRAL    = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return 0;
}
if(!calculador.getEp().getContratoFP()){
  return 0;
}
var lanc_he_50 = calculador.totalHorasExtras(50);
return calculador.calculaBaseSemRetroativoMesCompleto(''1031'') / calculador.obterHoraMensal()  * 1.5 *  calculador.totalHorasExtras(50);',
    VALORBASEDECALCULO      = 'return  calculador.calculaBaseSemRetroativoMesCompleto(''1031'');'
where codigo = '564'
