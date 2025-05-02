update eventofp set formula = replace(formula, 'valorTotalEventoOutrosVinculos(', 'valorTotalEventoOutrosVinculosIR(' )
where codigo = '901';

update eventofp set formula = replace(formula, 'valorTotalEventoOutrosVinculos(', 'valorTotalEventoOutrosVinculosIR(' )
where codigo = '418';

update eventofp set formula = replace(formula, 'valorTotalEventoOutrosVinculos(', 'valorTotalEventoOutrosVinculosIR(' )
where codigo = '500';
