update eventofp
set formula              = replace(FORMULA, 'var valor_desc_matric_1 = 0;', 'var valor_desc_matric_1 = 0;
if(calculador.recuperaTipoFolha() == ''RESCISAO''){
    valor_desc_matric_1 = calculador.buscarValorPorFolhaEvento(''VALOR'', new Array(''NORMAL''), new Array(''900''));
}'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, 'var valor_desc_matric_1 = 0;', 'var valor_desc_matric_1 = 0;
if(calculador.recuperaTipoFolha() == ''RESCISAO''){
    valor_desc_matric_1 = calculador.buscarValorPorFolhaEvento(''VALOR'', new Array(''NORMAL''), new Array(''900''));
}')
where codigo = '908'
