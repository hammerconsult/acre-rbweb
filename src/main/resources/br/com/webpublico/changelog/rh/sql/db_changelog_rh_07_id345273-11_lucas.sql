insert into EVENTOFP(ID, CODIGO, DESCRICAO, DESCRICAOREDUZIDA, TIPOEVENTOFP, REGRA, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, VALORBASEDECALCULO,
                     TIPOBASEFP_ID, TIPOCALCULO13, ATIVO, VERBAFIXA, AUTOMATICO, CALCULORETROATIVO, PROPORCIONALIZADIASTRAB, REMUNERACAOPRINCIPAL,
                     ARREDONDARVALOR, CONTROLECARGOLOTACAO, UNIDADEREFERENCIA, NAOPERMITELANCAMENTO, APURACAOIR, TIPOLANCAMENTOFP,QUANTIFICACAO,
                     VALORMAXIMOLANCAMENTO, ORDEMPROCESSAMENTO, TIPOEXECUCAOEP, DATAREGISTRO)
values (HIBERNATE_SEQUENCE.nextval, 935, 'PREVIDÊNCIA COMPLEMENTAR', 'PREV COMPLEMENTAR', 'DESCONTO',
        'return calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() ;',
        'var base = calculador.calculaBase(''1001'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base * calculador.aliquotaPrevidenciaComplementarServidor()  / 100;',
        'return 0;',
        'return calculador.aliquotaPrevidenciaComplementarServidor();',
        'var base = calculador.calculaBase(''1001'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base;', 133838384, 'NAO', 1, 0, 1, 0, 1, 0, 0, 0, null, 0, 0, 'REFERENCIA', 0, 10000, 898, 'FOLHA', sysdate)
