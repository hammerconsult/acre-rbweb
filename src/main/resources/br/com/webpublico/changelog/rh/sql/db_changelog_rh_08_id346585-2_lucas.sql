update eventofp
set regra                = 'return (calculador.recuperaTipoFolha() == ''SALARIO_13'' || calculador.recuperaTipoFolha() == ''RESCISAO'') && calculador.quantidadeMesesAfastadosPorAno(''3'') > 0;',
    formula              = 'var base = calculador.calculaBase(''1016'');
var mesesLicMater = calculador.quantidadeMesesAfastadosPorAno(''3'');
return base / 12 * mesesLicMater;',
    formulavalorintegral = 'return 0;',
    referencia           = 'var mesesLicMater = calculador.quantidadeMesesAfastadosPorAno(''3'');
return mesesLicMater;',
    valorbasedecalculo   = 'var base = calculador.calculaBase(''1016'');
return base;',
    tipocalculo13        = 'LER_FORMULA'
where codigo = '405'
