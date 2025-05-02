update eventofp
set VALORBASEDECALCULO = 'var base = 0;
if (calculador.recuperaTipoFolha() == ''SALARIO_13'') {
    base = calculador.calculaBase(''701631'');
} else {
    base = calculador.calculaBase(''1023'');
}

if (calculador.identificaPensionista() ) {
	base = calculador.calculaBaseInstituidorPensaoPrev();
}

var tetoRemuneratorio = calculador.tetoRemuneratorio().valor;
if (tetoRemuneratorio > 0) {
    if (base > tetoRemuneratorio){
        base = tetoRemuneratorio;
    }
} else {
    var tetoPrefeito = calculador.obterReferenciaValorFP(''22'').valor;
    if (base > tetoPrefeito) {
        base = tetoPrefeito;
    }
}

var tetoRPPS =  calculador.obterReferenciaValorFP(''13'').valor;

if ((calculador.identificaAposentado() && calculador.aposentadoInvalido()) || (calculador.identificaPensionista() && calculador.pensionistaInvalido())) {
    tetoRPPS = Number(tetoRPPS) * 2;
}

if (calculador.identificaAposentado() || calculador.identificaPensionista()){
    if (base > tetoRPPS) {
        base = Number(base) - Number(tetoRPPS);

        if (base > tetoRPPS) {
            base = tetoRPPS;
        }
        if (calculador.identificaPensionista()) {
            return Number(base * calculador.obterValorBasePensionista() / calculador.calculaBaseInstituidorPensaoPrev());
        }
        return Number(base);
    } else {
        return 0;
    }
}

if (calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > tetoRPPS) {
    if ((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023) {
        base = tetoRPPS;
    }
}
return Number(base);'
where CODIGO = 896
