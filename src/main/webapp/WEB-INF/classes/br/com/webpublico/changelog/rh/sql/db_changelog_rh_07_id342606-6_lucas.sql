insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4071, '%', 'IRRF PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'var base;

base = prestador.calculaBase(''60020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');

var vlrdeddep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

var baseded = base - vlrdeddep;
baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''60020'');
var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);
print(''base outros vinculos'' + prestador.buscarValorBaseOutrosVinculos(''60020''));


var descontar = prestador.valorTotalEventoOutrosVinculos(''4071'') +prestador.valorTotalEventoOutrosVinculos(''4005'') + calculador.valorTotalEventoOutrosVinculos(''901'');


if (ref.percentual > 0){
	var valor = (baseded * ref.percentual/100) - ref.valor;
	if(descontar > valor){
		return Number(0);
	}
   return Number(valor - descontar);

} else {
  return Number(0);

}', 'var base;

    base = prestador.calculaBase(''60020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');

    var vlrdeddep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

    var baseded = base - vlrdeddep;
    baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''60020'');
    var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);


    var descontar = prestador.valorTotalEventoOutrosVinculos(''4071'') +prestador.valorTotalEventoOutrosVinculos(''4005'') + calculador.valorTotalEventoOutrosVinculos(''901'');


    if (ref.percentual > 0){
        var valor = (baseded * ref.percentual/100) - ref.valor;
        if(descontar > valor){
            return Number(0);
        }
       return Number(valor - descontar);

    } else {
      return Number(0);

    }', 'var base;
    base = prestador.calculaBase(''60020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');
    var vlrdeddep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

    var baseded = base - vlrdeddep;

    baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''60020'');

    var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);

    if (ref.percentual > 0){
       return ref.percentual;
    } else {
      return parseFloat(0);
    }', 'var categoriaESocial = calculador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;', 'DESCONTO', 1, null, 'RPA', 'var base;

base = base = prestador.calculaBase(''60020'') +    calculador.valorTotalBaseOutrosVinculos(''1003'');
print(''base'' + calculaBaseFP_60020(ep));

base = base+ prestador.buscarValorBaseOutrosVinculos(''60020'');

           var vlrdeddep = (calculador.obterNumeroDependentes(''2'') + calculador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

           base = base - vlrdeddep;
return base;', 'IRRF AUT TRANSP', 1, null, 0, 0, 0, 'NAO', 0, 4071, 1,
        sysdate, 1, 1, null)
