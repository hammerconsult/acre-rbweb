update arquivoretornoobn350 set exercicio_id = (select id from exercicio where ano = to_char(dataimportacao, 'yyyy'))
