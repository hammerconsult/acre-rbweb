update eventofp
set formula = 'var base = calculador.calculaBase(''1001'');

var basePensionista = 0;

if ( calculador.identificaPensionista() ){
  if(base < calculador.calculaBaseInstituidorPensaoPrev()){
		base = calculador.calculaBaseInstituidorPensaoPrev();
  }
}

var tetoRemuneratorio = calculador.tetoRemuneratorio().valor;
if (tetoRemuneratorio > 0) {
    if(base > tetoRemuneratorio){
        base = tetoRemuneratorio;
    }
} else {
    var tetoPrefeito = calculador.obterReferenciaValorFP(''22'').valor;
    if (base > tetoPrefeito){
        base = tetoPrefeito;
    }
}

var tetoRPPS =  calculador.obterReferenciaValorFP(''13'').valor;
if( (calculador.identificaAposentado() && calculador.aposentadoInvalido()) || (calculador.identificaPensionista() && calculador.pensionistaInvalido()) ){
 tetoRPPS = tetoRPPS * 2;
}

var refe = calculador.obterReferenciaValorFP(''1'').valor;
if (calculador.identificaAposentado() || calculador.identificaPensionista()){

	if(base > tetoRPPS){
		base = base - tetoRPPS;
	 }else {
		return 0;
	 }
}

if(!calculador.identificaAposentado() && !calculador.identificaPensionista() && calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > tetoRPPS){
   base = tetoRPPS;
}


if(!calculador.identificaAposentado() && !calculador.identificaPensionista() && calculador.obterTipoPrevidenciaFP() == ''3'' && base > tetoRPPS && ((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023)){
    base = tetoRPPS;
}


	var calc = base * refe / 100;

if (calculador.identificaPensionista()){
 calc = (base * (refe / 100) * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
}

if ((calculador.identificaAposentado() || calculador.identificaPensionista()) && calc > tetoRPPS){
return tetoRPPS;
}
else{
return calc;
}'
where codigo = 898
