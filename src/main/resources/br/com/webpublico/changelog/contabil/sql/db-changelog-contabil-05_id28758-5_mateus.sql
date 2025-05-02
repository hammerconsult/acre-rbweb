update NOTAEXPLICATIVARGF set EXERCICIO_ID = (select id from exercicio where ano = 2019) where EXERCICIO_ID is null
