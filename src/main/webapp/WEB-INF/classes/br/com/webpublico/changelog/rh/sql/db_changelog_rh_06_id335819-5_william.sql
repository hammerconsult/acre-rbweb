update EVENTOFP set formula = 'if (calculador.temFuncaoGratificada(''COORDENACAO'')) {
    var calculoA = (calculador.obterValorFuncaoGratificadaPorTipo(''COORDENACAO'') / calculador.obterDiasDoMes()) * calculador.diasTrabalhadosFGPorTipo(''COORDENACAO'');
    return calculoA;
} else {
    var calculoB = (calculador.salarioBase() / calculador.obterDiasDoMes()) * calculador.diasTrabalhados();
    return calculoB;
}',
  FORMULAVALORINTEGRAL = 'if (calculador.temFuncaoGratificada(''COORDENACAO'')) {
    var calculoA = calculador.obterValorFuncaoGratificadaPorTipo(''COORDENACAO'');
    return calculoA;
} else {
    var calculoB = calculador.salarioBase();
    return calculoB;
}',
 REFERENCIA = 'if (calculador.temFuncaoGratificada(''COORDENACAO'')) {
    var calculoA = calculador.diasTrabalhadosFGPorTipo(''COORDENACAO'');
    return calculoA;
} else {
    var calculoB = calculador.diasTrabalhados();
    return calculoB;
}',
VALORBASEDECALCULO = 'if (calculador.temFuncaoGratificada(''COORDENACAO'')) {
    var calculoA = calculador.obterValorFuncaoGratificadaPorTipo(''COORDENACAO'');
    return calculoA;
} else {
    var calculoB = calculador.salarioBase();
    return calculoB;
}'
where CODIGO = '396'
