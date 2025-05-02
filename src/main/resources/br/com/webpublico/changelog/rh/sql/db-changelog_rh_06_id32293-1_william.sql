update eventofp set formula = replace(formula, 'if (desc_inss >= teto && valor_desc_matric_1 <= 0)', 'if (parseFloat(desc_inss).toFixed(2) >= teto && valor_desc_matric_1 <= 0)' )
where formula like '%if (desc_inss >= teto && valor_desc_matric_1 <= 0)%' and codigo = '900';

update eventofp set formula = replace(formula, 'if (desc_inss >= teto && valor_desc_matric_1 <= 0)', 'if (parseFloat(desc_inss).toFixed(2) >= teto && valor_desc_matric_1 <= 0)' )
where formula like '%if (desc_inss >= teto && valor_desc_matric_1 <= 0)%' and codigo = '899';

update eventofp set formula = replace(formula, 'if (desc_inss >= teto && valor_desc_matric_1 <= 0)', 'if (parseFloat(desc_inss).toFixed(2) >= teto && valor_desc_matric_1 <= 0)' )
where formula like '%if (desc_inss >= teto && valor_desc_matric_1 <= 0)%' and codigo = '908';
