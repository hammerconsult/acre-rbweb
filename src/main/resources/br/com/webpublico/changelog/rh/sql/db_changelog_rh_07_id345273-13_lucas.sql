insert into EVENTOFP(ID, CODIGO, DESCRICAO, DESCRICAOREDUZIDA, TIPOEVENTOFP, REGRA, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, VALORBASEDECALCULO,
                     TIPOBASEFP_ID, TIPOCALCULO13, ATIVO, VERBAFIXA, AUTOMATICO, CALCULORETROATIVO, PROPORCIONALIZADIASTRAB, REMUNERACAOPRINCIPAL,
                     ARREDONDARVALOR, CONTROLECARGOLOTACAO, UNIDADEREFERENCIA, NAOPERMITELANCAMENTO, APURACAOIR, TIPOLANCAMENTOFP,QUANTIFICACAO,
                     VALORMAXIMOLANCAMENTO, ORDEMPROCESSAMENTO, TIPOEXECUCAOEP, DATAREGISTRO)
values (HIBERNATE_SEQUENCE.nextval, 1235, 'PREVIDÊNCIA COMPLEMENTAR PATROCINADOR', 'PREV COMPLEM PATR', 'INFORMATIVO',
        'return calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() ;',
        'var base = calculador.calculaBase(''1001'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base * calculador.aliquotaPrevidenciaComplementarPatrocinador()  / 100;',
        'return 0;',
        'return calculador.aliquotaPrevidenciaComplementarPatrocinador();',
        'var base = calculador.calculaBase(''1001'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base;', 133838384, 'NAO', 1, 0, 1, 0, 1, 0, 0, 0, null, 0, 0, 'REFERENCIA', 0, 10000, 1235, 'FOLHA', sysdate)
