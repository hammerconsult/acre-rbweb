INSERT INTO EVENTOFP (ID, CODIGO, COMPLEMENTOREFERENCIA, DESCRICAO, FORMULA, FORMULAVALORINTEGRAL,
                      REFERENCIA, REGRA, TIPOEVENTOFP, AUTOMATICO, UNIDADEREFERENCIA, TIPOEXECUCAOEP,
                      TIPOBASE, VALORBASEDECALCULO, DESCRICAOREDUZIDA, ATIVO, QUANTIFICACAO,
                      TIPOLANCAMENTOFP, CALCULORETROATIVO, VERBAFIXA, NAOPERMITELANCAMENTO,
                      TIPOCALCULO13, TIPOBASEFP_ID, REFERENCIAFP_ID, ESTORNOFERIAS,
                      ORDEMPROCESSAMENTO, CONSIGNACAO, DATAREGISTRO, DATAALTERACAO,
                      TIPOPREVIDENCIAFP_ID, TIPODECONSIGNACAO, BLOQUEIOFERIAS, ARREDONDARVALOR,
                      PROPORCIONALIZADIASTRAB, EVENTOFPAGRUPADOR_ID, VALORMAXIMOLANCAMENTO,
                      IDENTIFICACAOEVENTOFP, PROPMESESTRABALHADOS, IDENTIFICACAOTABELA,
                      INICIOVIGENCIA, FIMVIGENCIA, NATUREZARUBRICA_ID, INCIDENCIAPREVIDENCIA_ID,
                      INCIDENCIATRIBUTARIAIRRF_ID, INCIDENCIATRIBUTARIAFGTS_ID,
                      INCIDENCIASINDICAL_ID, ENTIDADE_ID, ULTIMOACUMULADOEMDEZEMBRO, BASEFP_ID,
                      TIPOLANCAMENTOFPSIMPLIFICADO, CONTROLECARGOLOTACAO, NATUREZAREFENCIACALCULO,
                      VERBADEFERIAS, CLASSIFICACAOVERBA_ID)
VALUES (hibernate_sequence.nextval, '1102', null, '13º Salário Médias',
        'var valorAnual = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL''), ''1205'', true, 12, true);
var meses = calculador.quantidadeMesesTrabalhadosAno();
if(valorAnual <= 0){
    return 0;
}


return (valorAnual / 12);
',
        'var valorAnual = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL''), ''1205'', true, 12, true);
var meses = calculador.quantidadeMesesTrabalhadosAno();
if(valorAnual <= 0){
    return 0;
}


return (valorAnual / 12);
',
        'return calculador.quantidadeMesesTrabalhadosAno();',
        'var modalidade = calculador.obterModalidadeContratoFP();
if(modalidade.getCodigo() == 7){
    return false;
}
var mesFolha = (calculador.getMes() + 1);
var anoFolha = calculador.getAno();
if (mesFolha == calculador.mesFinalVigencia() && anoFolha ==  calculador.anoFinalVigencia() ) {
    return false;
}
if(calculador.quantidadeMesesTrabalhadosAno() <= 0){
    return false;
}

return true;',
        'VANTAGEM', 1,
        null, 'FOLHA', null, 'return calculador.salarioBase();', '13º Salário Médias', 1, 0, 'REFERENCIA', 0,
        0, 0, 'LER_FORMULA', null, null, 0, 300, 0,
        current_date,
        current_date, null, null, null, 1, 1, null, 70.00,
        null, 0, 'PMRBRB', TO_DATE('2020-09-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), null, null, null,
        null, null, 803705840, 8756990, 0, null, null, 0, null, 0, 10489910253)
