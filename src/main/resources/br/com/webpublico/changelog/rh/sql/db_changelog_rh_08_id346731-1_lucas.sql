update eventofp
set regra = 'return calculador.buscarTotalEventos(''ULTIMOSMESES'', ''REFERENCIA'', new Array(''NORMAL''), new Array(''441''), true, 0, true)  > 0 ;',
    formula = 'var base =  calculador.recuperaEvento(''896'', ''BASE'');
return base * calculador.buscarTotalEventos(''ULTIMOSMESES'', ''REFERENCIA'', new Array(''NORMAL''), new Array(''441''), true, 0, true) / 100;',
    formulavalorintegral = 'var base =  calculador.recuperaEvento(''896'', ''BASE'');
return base * calculador.buscarTotalEventos(''ULTIMOSMESES'', ''REFERENCIA'', new Array(''NORMAL''), new Array(''441''), true, 0, true) / 100;',
    referencia = 'return calculador.buscarTotalEventos(''ULTIMOSMESES'', ''REFERENCIA'', new Array(''NORMAL''), new Array(''441''), true, 0, true);',
    valorbasedecalculo = 'return calculador.recuperaEvento(''896'', ''BASE'');'
where codigo = '1105'
