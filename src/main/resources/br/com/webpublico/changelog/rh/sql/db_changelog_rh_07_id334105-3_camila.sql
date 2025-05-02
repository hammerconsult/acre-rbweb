
update EVENTOFP
set VALORBASEDECALCULO = 'var ded_apos_e_pens_65_anos = 0;

if(calculador.obterIdadeServidor() >= 65 && (calculador.identificaAposentado() || calculador.identificaPensionista()) ){

ded_apos_e_pens_65_anos = calculador.obterReferenciaValorFP(''25'').valor;

}

var vlr_ded_dep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''3'') + calculador.obterNumeroDependentes(''4'')  + calculador.obterNumeroDependentes(''10'')) * calculador.obterReferenciaValorFP(''3'').valor;
//base de IR não deduz dependente, somente para o valor final
return  calculador.calculaBaseMultiplosVinculos(''1203'') - ded_apos_e_pens_65_anos - vlr_ded_dep;'
where CODIGO = '500'
