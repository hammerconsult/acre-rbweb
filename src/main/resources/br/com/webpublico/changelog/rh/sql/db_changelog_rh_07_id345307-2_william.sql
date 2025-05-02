update eventofp
set regra                = 'return (parseFloat(calculador.totalHorasExtras(50) + calculador.totalHorasExtras(100)) > 0);',
    FORMULA              = 'var vlrHoras = calculador.avaliaEvento(''144'') + calculador.avaliaEvento(''145'') + calculador.avaliaEvento(''560'') + calculador.avaliaEvento(''561'');
var diasUteis = calculador.diasUteisMes();
var diasDomFer = calculador.diasDomingosFeriados();

return vlrHoras / diasUteis * diasDomFer;',
    FORMULAVALORINTEGRAL = 'return 0;',
    REFERENCIA           = 'var diasDomFer = calculador.diasDomingosFeriados();

return diasDomFer;',
    VALORBASEDECALCULO   = 'var vlrHoras = calculador.avaliaEvento(''144'') + calculador.avaliaEvento(''145'') + calculador.avaliaEvento(''560'') + calculador.avaliaEvento(''561'');

return vlrHoras;'
where codigo = '311'
