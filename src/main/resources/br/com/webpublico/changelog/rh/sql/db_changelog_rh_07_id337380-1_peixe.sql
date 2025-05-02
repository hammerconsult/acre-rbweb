update eventofp
set formula              = replace(FORMULA, 'var valorFolhaNormal = calculador.valorTotalItensFolhaNormal(''1018'');', 'var valorFolhaNormal = 0;'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, 'var valorFolhaNormal = calculador.valorTotalItensFolhaNormal(''1018'');', 'var valorFolhaNormal = 0;'),
    REFERENCIA           = replace(REFERENCIA, 'var valorFolhaNormal = calculador.valorTotalItensFolhaNormal(''1018'');', 'var valorFolhaNormal = 0;'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, 'var valorFolhaNormal = calculador.valorTotalItensFolhaNormal(''1018'');', 'var valorFolhaNormal = 0;')
where codigo = '908'
