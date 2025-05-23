update eventofp
set regra                = 'return (calculador.recuperaTipoFolha() == ''NORMAL'' || calculador.recuperaTipoFolha() == ''RESCISAO'') && calculador.quantidadeMesesAfastadosPorAno(''3'') > 0;',
    formula              = 'var base = calculador.calculaBase(''1016'');
var diasLicMater = calculador.diasDeAfastamento(''3'');
return base / calculador.obterDiasDoMes() * diasLicMater;',
    formulavalorintegral = 'var base = calculador.calculaBase(''1016'');
return base;',
    referencia           = 'var diasLicMater = calculador.diasDeAfastamento(''3'');
return diasLicMater;',
    valorbasedecalculo   = 'var base = calculador.calculaBase(''1016'');
return base;'
where codigo = '404'
