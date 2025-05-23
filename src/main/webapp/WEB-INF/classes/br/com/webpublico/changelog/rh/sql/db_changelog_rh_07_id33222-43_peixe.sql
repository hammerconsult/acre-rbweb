update EVENTOFP
set FORMULA              = 'var valorAnual = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL'',''COMPLEMENTAR''), ''1205'', true, 12, true);
var meses = calculador.quantidadeMesesTrabalhadosAno();
if(valorAnual <= 0){
    return 0;
}


return (valorAnual / 12);
',
    FORMULAVALORINTEGRAL = 'var valorAnual = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL'',''COMPLEMENTAR''), ''1205'', true, 12, true);
var meses = calculador.quantidadeMesesTrabalhadosAno();
if(valorAnual <= 0){
    return 0;
}


return (valorAnual / 12);
',
    VALORBASEDECALCULO   ='var valorAnual = calculador.buscarTotalBases(''SALARIO_13'', ''VALOR'', new Array(''NORMAL'',''COMPLEMENTAR''), ''1205'', true, 12, true);

return valorAnual;'
where codigo = '1102'
