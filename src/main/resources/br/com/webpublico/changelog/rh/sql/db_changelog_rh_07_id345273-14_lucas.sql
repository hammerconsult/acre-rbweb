insert into EVENTOFP(ID, CODIGO, DESCRICAO, DESCRICAOREDUZIDA, TIPOEVENTOFP, REGRA, FORMULA, FORMULAVALORINTEGRAL, REFERENCIA, VALORBASEDECALCULO,
                     TIPOBASEFP_ID, TIPOCALCULO13, ATIVO, VERBAFIXA, AUTOMATICO, CALCULORETROATIVO, PROPORCIONALIZADIASTRAB, REMUNERACAOPRINCIPAL,
                     ARREDONDARVALOR, CONTROLECARGOLOTACAO, UNIDADEREFERENCIA, NAOPERMITELANCAMENTO, APURACAOIR, TIPOLANCAMENTOFP,QUANTIFICACAO,
                     VALORMAXIMOLANCAMENTO, ORDEMPROCESSAMENTO, TIPOEXECUCAOEP, DATAREGISTRO)
values (HIBERNATE_SEQUENCE.nextval, 1236, 'PREVIDÊNCIA COMPLEMENTAR PATROCINADOR 13º', 'PREV COMPLEM PATR 13', 'INFORMATIVO',
        'return calculador.obterTipoPrevidenciaFP() == ''3'' && calculador.optantePrevidenciaComplementar() ;',
        'var base = calculador.calculaBase(''701631'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base * calculador.aliquotaPrevidenciaComplementarPatrocinador()  / 100;',
        'return 0;',
        'return calculador.aliquotaPrevidenciaComplementarPatrocinador();',
        'var base = calculador.calculaBase(''701631'') - calculador.obterReferenciaValorFP(''13'').valor;

if (base < 0) {
  return 0;
}

return base;', 133838384, 'LER_FORMULA', 1, 0, 1, 0, 0, 0, 0, 0, null, 0, 0, 'REFERENCIA', 200, 5000, 1236, 'FOLHA', sysdate)
