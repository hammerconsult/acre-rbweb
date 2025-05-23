update eventofp
set regra = 'if(calculador.identificaAposentado() || calculador.identificaPensionista()){
  return false;
}
if(!calculador.getEp().getContratoFP()){
  return false;
}
if(calculador.estaNoOrgao(''25'') || calculador.estaNoOrgao(''24'')){
  return false;
}
return calculador.totalHorasExtras(50) > 0;'
where codigo = '145'
