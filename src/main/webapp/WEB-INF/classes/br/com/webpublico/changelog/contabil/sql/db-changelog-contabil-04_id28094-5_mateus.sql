update DIARIACONTABILIZACAO set exercicio_id = (select id from exercicio where ano = extract(year from dataDiaria))
