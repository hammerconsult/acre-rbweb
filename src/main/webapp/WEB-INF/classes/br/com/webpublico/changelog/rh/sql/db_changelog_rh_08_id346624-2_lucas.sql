update eventofp
set formula    = replace(formula, 'calculador.diasDeAfastamento(''3'')', 'calculador.diasDeAfastamento(false, ''3'')'),
    referencia = replace(referencia, 'calculador.diasDeAfastamento(''3'')', 'calculador.diasDeAfastamento(false, ''3'')')
where codigo = '404';

update eventofp
set regra = 'return calculador.diasDeAfastamento(false, ''3'') > 0 || calculador.diasDeAfastamento(false, ''23'') > 0;',
    formula    = replace(formula, 'calculador.diasDeAfastamento(''1'')', 'calculador.diasDeAfastamento(false, ''1'')'),
    referencia = replace(referencia, 'calculador.diasDeAfastamento(''1'')', 'calculador.diasDeAfastamento(false, ''1'')')
where codigo = '306';
