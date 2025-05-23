update eventofp
set REGRA                = replace(regra, '1003', '1203'),
    FORMULA              = replace(FORMULA, '1003', '1203'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, '1003', '1203'),
    REFERENCIA           = replace(REFERENCIA, '1003', '1203'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, '1003', '1203')
where codigo = '500';

update eventofp
set REGRA                = replace(regra, '1001', '1202'),
    FORMULA              = replace(FORMULA, '1001', '1202'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, '1001', '1202'),
    REFERENCIA           = replace(REFERENCIA, '1001', '1202'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, '1001', '1202')
where codigo = '896';

update eventofp
set REGRA                = replace(regra, '1002', '1202'),
    FORMULA              = replace(FORMULA, '1002', '1202'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, '1002', '1202'),
    REFERENCIA           = replace(REFERENCIA, '1002', '1202'),
    VALORBASEDECALCULO   = replace(VALORBASEDECALCULO, '1002', '1202')
where codigo = '899';
