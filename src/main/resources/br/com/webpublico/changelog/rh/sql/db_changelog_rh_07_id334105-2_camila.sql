
update eventofp
set VALORBASEDECALCULO = 'var vlr_ded_dep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''3'') + calculador.obterNumeroDependentes(''4'')) * calculador.obterReferenciaValorFP(''3'').valor;
return  calculador.calculaBaseMultiplosVinculos(''1070'') - vlr_ded_dep;'
where CODIGO = '418'
