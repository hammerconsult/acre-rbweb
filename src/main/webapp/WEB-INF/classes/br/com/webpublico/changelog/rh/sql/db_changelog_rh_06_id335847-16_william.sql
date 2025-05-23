update EVENTOFP set REGRA = 'var modalidade = calculador.obterModalidadeContratoFP(); return modalidade.getCodigo() == ''3'' || modalidade.getCodigo() == ''7'' || calculador.temAcessoSubsidio();'
 where CODIGO = '102';
