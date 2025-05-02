update eventofp
set REGRA                = replace(regra, '1005', '1204'),
    FORMULA              = replace(FORMULA, '1005', '1204'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, '1005', '1204'),
    REFERENCIA           = replace(REFERENCIA, '1005', '1204'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, '1005', '1204')
where codigo = '897';
