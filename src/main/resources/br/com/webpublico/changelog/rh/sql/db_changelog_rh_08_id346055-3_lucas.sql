update eventofp
set FORMULA = replace(FORMULA,
                      'if(calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > tetoRPPS){',
                      'if(!calculador.identificaAposentado() && !calculador.identificaPensionista() && calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() && base > tetoRPPS){')
where CODIGO = 896;

update eventofp
set FORMULA = replace(FORMULA,
                      'if(calculador.obterTipoPrevidenciaFP() == ''3'' && base > tetoRPPS && ((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023)){',
                      'if(!calculador.identificaAposentado() && !calculador.identificaPensionista() && calculador.obterTipoPrevidenciaFP() == ''3'' && base > tetoRPPS && ((calculador.mesInicioVigenciaVinculo() >= 11 && calculador.anoInicioVigenciaVinculo() == 2023) || calculador.anoInicioVigenciaVinculo() > 2023)){')
where CODIGO = 896;
