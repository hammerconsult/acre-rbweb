update EVENTOFP
set FORMULA = 'if(calculador.obterTipoPrevidenciaFP()) {
 	if(calculador.obterTipoPrevidenciaFP() != ''3''){
	return 0;
	}
}

if(calculador.recuperaTipoFolha() == ''SALARIO_13''){
  var base = calculador.calculaBase(''701631'');

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

if (base > teto){
    base = teto;
}
	var calc = base * refe / 100;

if (calculador.identificaPensionista()){
 calc = (base * (refe / 100) * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
}

if (calc > teto){
return teto;
}
else{
return calc;
}
}


var base = calculador.calculaBase(''1023'');


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

if (calc > teto){
return teto;
}
else{
return calc;
}',
    VALORBASEDECALCULO = 'var base = 0;
if(calculador.recuperaTipoFolha() == ''SALARIO_13''){
 base = calculador.calculaBase(''701631'');
}else {
 base = calculador.calculaBase(''1023'');
}



if ( calculador.identificaPensionista() ){
		base = calculador.calculaBaseInstituidorPensaoPrev();
}

var teto =  calculador.obterReferenciaValorFP(''13'').valor;

if( (calculador.identificaAposentado() && calculador.aposentadoInvalido()) || (calculador.identificaPensionista() && calculador.pensionistaInvalido()) ){
 teto = Number(teto) * 2;

}

if (calculador.identificaAposentado() || calculador.identificaPensionista()){

 if(base > teto) {
	base = Number(base) - Number(teto);
                base = base;

    if (base > teto){
    base = teto;
    }
    if (calculador.identificaPensionista()){
        return Number(base * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
    }
    return Number(base);
 } else {
 	return 0;
 }
}

if (calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > teto){
    if((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023){
        base = teto;
    }
}
return Number(base);'
where CODIGO = 896
