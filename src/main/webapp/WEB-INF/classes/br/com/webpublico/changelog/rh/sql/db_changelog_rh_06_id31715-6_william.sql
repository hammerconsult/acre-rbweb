update eventofp set regra = 'var modalidade = calculador.obterModalidadeContratoFP(); if(modalidade.getCodigo() == 1 && calculador.estaNoOrgao(''12'') && calculador.identificaTipoPeq(''MAGISTERIO'') && (calculador.obterCodigoSituacaoFuncional() == 1 || calculador.obterCodigoSituacaoFuncional() == 2)){     return true;} else {     return false; }'
where codigo = '423';

update eventofp set formula = 'return (calculador.obterReferenciaValorFP(''33'').valor / calculador.quantidadeTipoPeq(''MAGISTERIO'')) / calculador.obterDiasDoMes() * calculador.diasTrabalhados();'
where codigo = '423';

update eventofp set FORMULAVALORINTEGRAL = 'return calculador.obterReferenciaValorFP(''33'').valor / calculador.quantidadeTipoPeq(''MAGISTERIO'');'
where codigo = '423';

update eventofp set referencia = 'return calculador.diasTrabalhados();'
where codigo = '423';

update eventofp set VALORBASEDECALCULO = 'return calculador.obterReferenciaValorFP(''33'').valor;'
where codigo = '423';

update eventofp set propMesesTrabalhados = 1
where codigo = '423';

update eventofp set automatico = 1
where codigo = '423';


update eventofp set regra = 'var modalidade = calculador.obterModalidadeContratoFP(); if(modalidade.getCodigo() == 1 && calculador.estaNoOrgao(''12'') && calculador.identificaTipoPeq(''APOIO'') && (calculador.obterCodigoSituacaoFuncional() == 1 || calculador.obterCodigoSituacaoFuncional() == 2)){    return true; } else {    return false; }'
where codigo = '424';

update eventofp set formula = 'return (calculador.obterReferenciaValorFP(''34'').valor / calculador.quantidadeTipoPeq(''APOIO'')) / calculador.obterDiasDoMes() * calculador.diasTrabalhados();'
where codigo = '424';

update eventofp set FORMULAVALORINTEGRAL = 'return calculador.obterReferenciaValorFP(''34'').valor / calculador.quantidadeTipoPeq(''APOIO'');'
where codigo = '424';

update eventofp set referencia = 'return calculador.diasTrabalhados();'
where codigo = '424';

update eventofp set VALORBASEDECALCULO = 'return calculador.obterReferenciaValorFP(''34'').valor;'
where codigo = '424';

update eventofp set propMesesTrabalhados = 1
where codigo = '424';

update eventofp set automatico = 1
where codigo = '424';


