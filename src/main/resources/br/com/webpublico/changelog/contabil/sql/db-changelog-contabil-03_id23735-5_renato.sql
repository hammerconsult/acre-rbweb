update movimentodividapublica set exercicio_id = (select id from exercicio where ano = to_char(data, 'yyyy'))
