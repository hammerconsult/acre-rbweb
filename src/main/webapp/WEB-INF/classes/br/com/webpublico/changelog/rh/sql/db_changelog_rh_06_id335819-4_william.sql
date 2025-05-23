update EVENTOFP set REGRA = 'return calculador.obterModalidadeContratoFP().codigo == 9 || calculador.temFuncaoGratificada(''COORDENACAO'');' where CODIGO = '396';

update EVENTOFP set formula = 'if(calculador.temFuncaoGratificada(''COORDENACAO'')) {var calculoA = (calculador.obterValorFuncaoGratificada(''COORDENACAO'') / calculador.obterDiasDoMes()) * calculador.diasTrabalhadosFG(''COORDENACAO''); return calculoA;} else {    var calculoB = (calculador.salarioBase() / calculador.obterDiasDoMes()) * calculador.diasTrabalhados();    return calculoB;}' where CODIGO = '396';

update EVENTOFP set FORMULAVALORINTEGRAL = 'if(calculador.temFuncaoGratificada(''COORDENACAO'')) { var calculoA = calculador.obterValorFuncaoGratificada(''COORDENACAO'');    return calculoA;} else {    var calculoB = calculador.salarioBase();    return calculoB;}'
 where CODIGO = '396';

update EVENTOFP set REFERENCIA = 'if(calculador.temFuncaoGratificada(''COORDENACAO'')) {    var calculoA = calculador.diasTrabalhadosFG(''COORDENACAO'');    return calculoA;} else {    var calculoB = calculador.diasTrabalhados();    return calculoB;}'
where CODIGO = '396';

update EVENTOFP set VALORBASEDECALCULO = 'if(calculador.temFuncaoGratificada(''COORDENACAO'')) {    var calculoA = calculador.obterValorFuncaoGratificada(''COORDENACAO'');    return calculoA;} else {    var calculoB = calculador.salarioBase();    return calculoB;}'
 where CODIGO = '396';
