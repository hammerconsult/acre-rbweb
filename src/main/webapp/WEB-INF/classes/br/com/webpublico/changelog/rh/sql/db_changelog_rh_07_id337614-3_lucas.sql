update eventofp set FORMULA = 'return calculador.obterValorPensionista(''600'');'
where CODIGO = '600';

update eventofp set referencia = 'return calculador.obterReferenciaPensionista(''600'');'
where codigo = '600';
