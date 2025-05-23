update eventofp
set formula              = replace(FORMULA, 'else if (desc_inss > teto && valor_desc_matric_1 > 0){', 'else if (desc_inss >= teto && valor_desc_matric_1 > 0){'),
    FORMULAVALORINTEGRAL = replace(FORMULAVALORINTEGRAL, 'else if (desc_inss > teto && valor_desc_matric_1 > 0){', 'else if (desc_inss >= teto && valor_desc_matric_1 > 0){')
where codigo = '908'
