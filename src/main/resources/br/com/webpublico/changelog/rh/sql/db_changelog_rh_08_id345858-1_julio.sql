update eventofp
set FORMULA = 'var ded_apos_e_pens_65_anos = 0;

if(calculador.obterIdadeServidor() >= 65 && (calculador.identificaAposentado() || calculador.identificaPensionista()) ){

ded_apos_e_pens_65_anos = calculador.obterReferenciaValorFP(''25'').valor;

}

var vlr_base =  calculador.calculaBaseMultiplosVinculos(''1003'') - ded_apos_e_pens_65_anos;

var vlr_ded_dep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''3'') + calculador.obterNumeroDependentes(''4'')  + calculador.obterNumeroDependentes(''10'')) * calculador.obterReferenciaValorFP(''3'').valor;

var vlr_base_ded = vlr_base - vlr_ded_dep;

var vlr_desc_matric1 = calculador.valorTotalEventoOutrosVinculos(''901'');

var vlr_ref_perc = calculador.obterReferenciaFaixaFP(''2'',vlr_base_ded).percentual;

var vlr_ref_valor = calculador.obterReferenciaFaixaFP(''2'',vlr_base_ded).valor;


if (vlr_ref_perc > 0){
  var valorFinal = ((vlr_base_ded * vlr_ref_perc / 100) - vlr_ref_valor) - vlr_desc_matric1;
if(valorFinal <= 0.0){
return 0;
 } else return valorFinal;
}
else {
  return 0;
}'
where CODIGO = 901
