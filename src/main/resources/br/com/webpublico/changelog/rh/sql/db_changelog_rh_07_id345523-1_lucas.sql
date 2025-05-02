update EVENTOFP
set FORMULA = 'var base = calculador.calculaBase(''1001'');

var basePensionista = 0;

if ( calculador.identificaPensionista() ){
  if(base < calculador.calculaBaseInstituidorPensaoPrev()){
		base = calculador.calculaBaseInstituidorPensaoPrev();
  }
}

var teto =  calculador.obterReferenciaValorFP(''13'').valor;
if( (calculador.identificaAposentado() && calculador.aposentadoInvalido()) || (calculador.identificaPensionista() && calculador.pensionistaInvalido()) ){
 teto = teto * 2;
}

var refe = calculador.obterReferenciaValorFP(''1'').valor;
if (calculador.identificaAposentado() || calculador.identificaPensionista()){

	if(base > teto){
		base = base - teto;
	 }else {
		return 0;
	 }
}

if (calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > teto){
    if((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023){
        base = teto;
    }
}

	var calc = base * refe / 100;

if (calculador.identificaPensionista()){
 calc = (base * (refe / 100) * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
}

if ((calculador.identificaAposentado() || calculador.identificaPensionista()) && calc > teto){
return teto;
}
else{
return calc;
}',
    VALORBASEDECALCULO = 'var base = calculador.calculaBase(''1001'');

var basePensionista = 0;

if ( calculador.identificaPensionista() ){
  if(base < calculador.calculaBaseInstituidorPensaoPrev()){
		base = calculador.calculaBaseInstituidorPensaoPrev();
  }
}

var teto =  calculador.obterReferenciaValorFP(''13'').valor;
if( (calculador.identificaAposentado() && calculador.aposentadoInvalido()) || (calculador.identificaPensionista() && calculador.pensionistaInvalido()) ){
 teto = teto * 2;
}

if (calculador.identificaAposentado() || calculador.identificaPensionista()){

	if(base > teto){
		base = base - teto;
	 } else {
		return 0;
	 }
}

if (calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > teto){
    if((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023){
        base = teto;
    }
}

return base;'
where CODIGO = 898
