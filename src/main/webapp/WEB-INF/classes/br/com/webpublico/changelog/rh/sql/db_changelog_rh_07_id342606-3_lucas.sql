insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4051, '%', 'IRRF PRESTAÇÃO SERVIÇO NORMAL',
        'var base;

base = prestador.calculaBase(''50020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');

var vlrdeddep = (prestador.obterNumeroDependentes(''2'') + prestador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

var baseded = base - vlrdeddep;
baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''50020'');
var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);


var descontar = prestador.valorTotalEventoOutrosVinculos(''4051'') +prestador.valorTotalEventoOutrosVinculos(''4005'') + calculador.valorTotalEventoOutrosVinculos(''901'');


if (ref.percentual > 0){
	var valor = (baseded * ref.percentual/100) - ref.valor;
	if(descontar > valor){
		return Number(0);
	}
   return Number(valor - descontar);

} else {
  return Number(0);

}',
        'var base;

    base = prestador.calculaBase(''50020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');

    var vlrdeddep = (prestador.obterNumeroDependentes(''2'') + prestador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

    var baseded = base - vlrdeddep;
    baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''50020'');
    var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);


    var descontar = prestador.valorTotalEventoOutrosVinculos(''4051'') + prestador.valorTotalEventoOutrosVinculos(''4005'') + calculador.valorTotalEventoOutrosVinculos(''901'');


    if (ref.percentual > 0){
        var valor = (baseded * ref.percentual/100) - ref.valor;
        if(descontar > valor){
            return Number(0);
        }
       return Number(valor - descontar);

    } else {
      return Number(0);

    }',
        'var base;
    base = prestador.calculaBase(''50020'')+  calculador.valorTotalBaseOutrosVinculos(''1003'');
    var vlrdeddep = (prestador.obterNumeroDependentes(''2'') + prestador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

    var baseded = base - vlrdeddep;

    baseded = baseded+ prestador.buscarValorBaseOutrosVinculos(''50020'');

    var ref = calculador.obterReferenciaFaixaFP(''2'',baseded);

    if (ref.percentual > 0){
       return ref.percentual;
    } else {
      return parseFloat(0);
    }',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 701 || categoriaESocial == 738 || categoriaESocial == 741 || categoriaESocial == 751 || categoriaESocial == 761 || categoriaESocial == 771 || categoriaESocial == 781 || categoriaESocial == 902 || categoriaESocial == 731;',
        'DESCONTO', 1, '', 'RPA',
        'var base;

base = prestador.calculaBase(''50020'') +  calculador.valorTotalBaseOutrosVinculos(''1003'');

base = base+ prestador.buscarValorBaseOutrosVinculos(''50020'');

           var vlrdeddep = (prestador.obterNumeroDependentes(''2'') + prestador.obterNumeroDependentes(''10''))  * calculador.obterReferenciaValorFP(''3'').valor;

           base = base - vlrdeddep;
return base;', 'IRRF PRE SERV NORMAL', 1, 0, 0, 0, 0, 'NAO', 0, 4051, 1, SYSDATE, 1, 1, 0)
