update CONFIGURACAOCONTABIL set CONTAEXTRAINSSPADRAODOCLIQ_ID = (select id from conta where codigo = '9.9.0.0.1.00.00' and exercicio_id = (select id from exercicio where ano = 2024));
update CONFIGURACAOCONTABIL set CONTAEXTRAIRRFPADRAODOCLIQ_ID = (select id from conta where codigo = '9.9.0.0.2.00.00' and exercicio_id = (select id from exercicio where ano = 2024));
