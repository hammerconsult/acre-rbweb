insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4070, '%', 'INSS PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'var base = prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''50010'');

base = base + prestador.buscarValorBaseOutrosVinculos(''60010'');

var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

    var desconto1 = prestador.valorTotalEventoOutrosVinculos(''4070'');
    var desconto2 =  prestador.valorTotalEventoOutrosVinculos(''4003'');
    var desconto3 =  calculador.valorTotalEventoOutrosVinculos(''900'');
    var desconto4 =  prestador.valorTotalEventoOutrosVinculos(''4050'');

    var descontar =  desconto1 + desconto2 + desconto3 + desconto4;

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

var valorBruto =  (refe.percentual/100) * base;

if ( (valorBruto - descontar) > refe.valor) {
   return refe.valor;
} else {
   var valorFinal = (valorBruto - descontar);
   return valorFinal;
}',
        'var base = prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''50010'');

base = base + prestador.buscarValorBaseOutrosVinculos(''60010'');

var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

    var desconto1 = prestador.valorTotalEventoOutrosVinculos(''4070'');
    var desconto2 =  prestador.valorTotalEventoOutrosVinculos(''4003'');
    var desconto3 =  calculador.valorTotalEventoOutrosVinculos(''900'');
    var desconto4 =  prestador.valorTotalEventoOutrosVinculos(''4050'');


    var descontar =  desconto1 + desconto2 + desconto3 + desconto4;

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

var valorBruto =  (refe.percentual/100) * base;

if ( (valorBruto - descontar) > refe.valor) {
   return refe.valor;
} else {
   var valorFinal = (valorBruto - descontar);
   return valorFinal;
}',
        'var base = prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''50010'');

base = base + prestador.buscarValorBaseOutrosVinculos(''60010'');


var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
    base = baseTetoInss;
}

var refe = calculador.obterReferenciaFaixaFP(''12'', base.doubleValue());

return refe.percentual;',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;', 'DESCONTO', 1, null, 'RPA',
        'return  prestador.calculaBase(''60010'')  + calculador.valorTotalBaseOutrosVinculos(''1002'') + prestador.buscarValorBaseOutrosVinculos(''50010'');', 'INSS AUT TRANSP', 1, null, 0, 0, 0, 'NAO', 0, 4070, 1,
        SYSDATE,
        0, 1, null)
