update EVENTOFP
set regra              = 'return (calculador.temFeriasConcedidas() || calculador.diasSaldo13FeriasAut() > 0);',
    formula            = 'var eventos = new Array(''311'');
var vlrTotal = calculador.buscarTotalEventos(''FERIAS'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
var duracaoBasePA = calculador.duracaoBasePeriodoAquisitivo(''FERIAS'');
var diasDireitoBasePA = calculador.quantidadeDiasDireitoBasePA(''FERIAS'');
var diasFer = calculador.avaliaReferencia(''333'');

return vlrTotal / duracaoBasePA / diasDireitoBasePA * diasFer;',
    REFERENCIA         = 'var diasSld13FerAut = Number(calculador.diasSaldo13FeriasAut());
var diasFerNaoLancAut = Number(calculador.quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente());
var diasFerCon = Number(calculador.quantidadeDiasFeriasConcedidas());
var diasFerAbono =  Number(calculador.quantidadeDiasAbonoPecuniarioFerias());
var diasFer = diasSld13FerAut + diasFerNaoLancAut + diasFerCon + diasFerAbono;

return diasFer;',
    VALORBASEDECALCULO = 'var vlrTotal = calculador.buscarTotalEventos(''FERIAS'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
return vlrTotal;'
where codigo = '333'
