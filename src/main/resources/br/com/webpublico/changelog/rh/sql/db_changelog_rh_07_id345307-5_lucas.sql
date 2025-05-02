update EVENTOFP
set VALORBASEDECALCULO = 'var eventos = new Array(''311'');
var vlrTotal = calculador.buscarTotalEventos(''FERIAS'', ''VALOR'', new Array(''NORMAL''), eventos, false, 0, false);
return vlrTotal;'
where codigo = '333'
