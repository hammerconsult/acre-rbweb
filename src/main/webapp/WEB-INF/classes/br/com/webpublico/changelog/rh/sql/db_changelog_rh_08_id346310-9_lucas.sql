update EVENTOFP
set regra = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return false;
}
if(!calculador.getEp().getContratoFP()){
  return false;
}
if(!calculador.estaNoOrgao(''24'') || (calculador.estaNoOrgao(''24'') && calculador.obterModalidadeContratoFP().codigo != 4)){
  return false;
}
return calculador.totalHorasExtras(50) > 0;'
where CODIGO = '564'
