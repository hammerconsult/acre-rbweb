update EVENTOFP set FORMULA = 'if(calculador.temAcessoSubsidio()){    var dias = 0;    if(calculador.diasTrabalhadosAS() == 0){    dias = calculador.diasTrabalhados();    } else {    dias = calculador.diasTrabalhadosAS();    }    if(calculador.codigoOpcaoSalarial(''SUBSIDIO'') == ''16''){    return ((calculador.valorSubsidioPCS() * calculador.recuperarPercentualOpcaoSalarial(''SUBSIDIO'') / 100) / calculador.obterDiasDoMes()) * dias;    }    var codigoBaseCargoConfianca = calculador.codigoBaseFPSubsidio();    if(codigoBaseCargoConfianca == null){       codigoBaseCargoConfianca = ''1014'';    }    var valor = (calculador.valorSubsidioPCS() - calculador.calculaBaseSemRetroativo(codigoBaseCargoConfianca)) / calculador.obterDiasDoMes() * dias;    if(valor < 0){     return 0;    } else {     return valor;    } } else {    var calculoB = (calculador.salarioBase() / calculador.obterDiasDoMes()) * calculador.diasTrabalhados();    return calculoB;}'
 where CODIGO = '102';

update EVENTOFP set FORMULAVALORINTEGRAL = 'if(calculador.temAcessoSubsidio()){    return calculador.valorSubsidioPCS(); } else {    return calculador.salarioBase(); }' where CODIGO = '102';

update EVENTOFP set REFERENCIA = 'if(calculador.temAcessoSubsidio()){    return calculador.diasTrabalhadosAS();} else {    return calculador.diasTrabalhados();}'
 where CODIGO = '102';

update EVENTOFP set VALORBASEDECALCULO = 'if(calculador.temAcessoSubsidio()){    return calculador.valorSubsidioPCS();} else {    return calculador.salarioBase();}'
 where CODIGO = '102';
