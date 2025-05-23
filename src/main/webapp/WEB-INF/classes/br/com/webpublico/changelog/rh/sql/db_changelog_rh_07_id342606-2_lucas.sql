insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     AUTOMATICO, TIPOEVENTOFP, UNIDADEREFERENCIA, TIPOEXECUCAOEP, TIPOBASE, VALORBASEDECALCULO,
                     DESCRICAOREDUZIDA, ATIVO, QUANTIFICACAO, TIPOLANCAMENTOFP, CALCULORETROATIVO, VERBAFIXA,
                     NAOPERMITELANCAMENTO, TIPOCALCULO13, TIPOBASEFP_ID, REFERENCIAFP_ID, ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, DATAALTERACAO, TIPOPREVIDENCIAFP_ID,
                     TIPODECONSIGNACAO, BLOQUEIOFERIAS, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB, EVENTOFPAGRUPADOR_ID,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4050, '%', 'INSS PRESTAÇÃO SERVIÇO NORMAL', 'var base = prestador.calculaBase(''50010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''60010'');

base = base + prestador.buscarValorBaseOutrosVinculos(''50010'');

var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

    var desconto1 = prestador.valorTotalEventoOutrosVinculos(''4050'');
    var desconto2 =  prestador.valorTotalEventoOutrosVinculos(''4003'');
    var desconto3 =  calculador.valorTotalEventoOutrosVinculos(''900'');
    var desconto4 =  prestador.valorTotalEventoOutrosVinculos(''4070'');


    var descontar =  desconto1 + desconto2 + desconto3 + desconto4;

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

var valorBruto =  (refe.percentual/100) * base;

if ( (valorBruto - descontar) > refe.valor) {
   return refe.valor;
} else {
   var valorFinal = (valorBruto - descontar);
   return valorFinal;
}', 'var base = prestador.calculaBase(''50010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''60010'');

base = base + prestador.buscarValorBaseOutrosVinculos(''50010'');

var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

    var desconto1 = prestador.valorTotalEventoOutrosVinculos(''4050'');
    var desconto2 =  prestador.valorTotalEventoOutrosVinculos(''4003'');
    var desconto3 =  calculador.valorTotalEventoOutrosVinculos(''900'');
    var desconto4 =  prestador.valorTotalEventoOutrosVinculos(''4070'');


    var descontar =  desconto1 + desconto2 + desconto3 + desconto4;

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

var valorBruto =  (refe.percentual/100) * base;

if ( (valorBruto - descontar) > refe.valor) {
   return refe.valor;
} else {
   var valorFinal = (valorBruto - descontar);
   return valorFinal;
}', 'var base = prestador.calculaBase(''50010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''60010'');
base = base + prestador.buscarValorBaseOutrosVinculos(''50010'');


var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

return refe.percentual;', 'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 701 || categoriaESocial == 738 || categoriaESocial == 741 || categoriaESocial == 751 || categoriaESocial == 761 || categoriaESocial == 771 || categoriaESocial == 781 || categoriaESocial == 902 || categoriaESocial == 731;',
        1, 'DESCONTO', '', 'RPA', null, 'return  prestador.calculaBase(''50010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''60010'');', 'INSS PRE SERV NORMAL',
        1, 0, null, 0, 0, 0, 'NAO', null, null, 0, 4050, 1, sysdate, null, null, null, null, 0, 1, null, 0)
