update EVENTOFP set REGRA = 'if(calculador.quantidadeTipoPeq(''MAGISTERIO'') <= 0){ return false; } var modalidade = calculador.obterModalidadeContratoFP(); if(modalidade.getCodigo() == 1 && calculador.estaNoOrgao(''12'') && calculador.identificaTipoPeq(''MAGISTERIO'') && (calculador.obterCodigoSituacaoFuncional() == 1 || calculador.obterCodigoSituacaoFuncional() == 2 || calculador.estaAfastado(3, 23, 24, 25, 26, 37, 38, 1, 13, 17, 18, 27, 32, 36, 10, 21) || calculador.estaCedido(''CONUS''))) { return true; } else { return false; }'
where CODIGO = '423';

update EVENTOFP set REGRA = 'if(calculador.quantidadeTipoPeq(''APOIO'') <= 0) { return false; } var modalidade = calculador.obterModalidadeContratoFP(); if(modalidade.getCodigo() == 1 && calculador.estaNoOrgao(''12'') && calculador.identificaTipoPeq(''APOIO'') && (calculador.obterCodigoSituacaoFuncional() == 1 || calculador.obterCodigoSituacaoFuncional() == 2 || calculador.estaAfastado(3, 23, 24, 25, 26, 37, 38, 1, 13, 17, 18, 27, 32, 36, 10, 21) || calculador.estaCedido(''CONUS''))) { return true; } else { return false; }'
where CODIGO = '424';

