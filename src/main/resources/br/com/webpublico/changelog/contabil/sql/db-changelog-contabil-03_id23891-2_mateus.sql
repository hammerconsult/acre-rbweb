update INVESTIMENTO set exercicio_id = (select id from exercicio where ano = 2017) where exercicio_id is null
