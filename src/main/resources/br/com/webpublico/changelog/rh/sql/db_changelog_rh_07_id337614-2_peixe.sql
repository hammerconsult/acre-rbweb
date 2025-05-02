update eventofp
set FORMULA = replace(FORMULA,
                      'return calculador.obterValorPensionista(''600'')  / calculador.obterDiasDoMes() * calculador.diasTrabalhados();',
                      'return calculador.obterValorPensionista(''600'');')
where CODIGO in ('600');
update eventofp
set FORMULA = replace(FORMULA,
                      'return calculador.obterValorPensionista(''355'')  / calculador.obterDiasDoMes() * calculador.diasTrabalhados();',
                      'return calculador.obterValorPensionista(''355'');')
where CODIGO in ('355');
