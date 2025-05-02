update hierarquiaorganizacional set exercicio_id = (select id from exercicio where ano = extract(year from coalesce(fimvigencia,sysdate))) where exercicio_id is null
