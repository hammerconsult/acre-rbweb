update eventofp
set regra = 'return (calculador.quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente() > 0 || calculador.diasSaldo13FeriasAut() > 0);',
    formula = 'var eventos = new Array(''311'');
var vlrTotal = 0;

if(calculador.diasSaldo13FeriasAut() > 0) {
    vlrTotal = calculador.buscarTotalEventos(''FERIASAUT13'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
} else {
    vlrTotal = calculador.buscarTotalEventos(''FERIAS'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
}

var duracaoBasePA = calculador.duracaoBasePeriodoAquisitivo(''FERIAS'');
var diasDireitoBasePA = calculador.quantidadeDiasDireitoBasePA(''FERIAS'');
var diasFer = calculador.avaliaReferencia(''333'');

return vlrTotal / duracaoBasePA / diasDireitoBasePA * diasFer;',
    formulavalorintegral = 'return 0;',
    referencia = 'var diasSld13FerAut = Number(calculador.diasSaldo13FeriasAut());
var diasFerNaoLancAut = Number(calculador.quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente());
var diasFerAbono =  Number(calculador.quantidadeDiasAbonoPecuniarioFerias());
var diasFer = 0;

if(diasSld13FerAut > 0) {
    diasFer = diasSld13FerAut;
} else {
    diasFer = diasFerNaoLancAut;
    if(diasFer > 0) {
        diasFer = diasFer + diasFerAbono;
    }
}

return diasFer;',
    valorbasedecalculo = 'var eventos = new Array(''311'');

if(calculador.diasSaldo13FeriasAut() > 0) {
    return calculador.buscarTotalEventos(''FERIASAUT13'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
}

return calculador.buscarTotalEventos(''FERIAS'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);'
where codigo = '333'
