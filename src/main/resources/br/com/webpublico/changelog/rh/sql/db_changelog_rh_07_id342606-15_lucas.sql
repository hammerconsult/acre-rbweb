insert into EVENTOFP(ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, REGRA,
                     TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP, VALORBASEDECALCULO, DESCRICAOREDUZIDA,
                     ATIVO, QUANTIFICACAO, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO, TIPOCALCULO13,
                     ESTORNOFERIAS,
                     ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, ARREDONDARVALOR, PROPORCIONALIZADIASTRAB,
                     VALORMAXIMOLANCAMENTO)
values (HIBERNATE_SEQUENCE.nextval, 4080, '%', 'SEST PRESTAÇÃO SERVIÇO TRANSPORTADOR',
        'var base = prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002'');
var baseTetoInss = calculador.obterReferenciaFaixaFP(''8'', 6000).referenciaAte;

if(base > baseTetoInss){
base = baseTetoInss;
}

var desconto = prestador.valorTotalEventoOutrosVinculos(''4080'');

var valorBruto = base * (prestador.avaliaReferencia(''4080'') / 100);
var valorFinal = valorBruto - desconto;

if (valorFinal < 0) {
return 0.0;
} else {
return valorFinal;
}',
        'return 0;',
        'return 1.5;',
        'var categoriaESocial = prestador.obterCategoriaeSocial();

return categoriaESocial == 711 || categoriaESocial == 712 || categoriaESocial == 734;',
        'DESCONTO',
        1, '%', 'RPA',
        'return  prestador.calculaBase(''60010'') + calculador.valorTotalBaseOutrosVinculos(''1002''); ', 'SEST PRES TRANSPORTADOR', 1, 0, 0, 0, 0, 'NAO', 0, 4080, 1, SYSDATE, 1, 1, 0)
