update conta c set c.codigo = substr(c.codigo, 3, length(c.codigo))
where c.DTYPE = 'ContaDeDestinacao'
and c.exercicio_id = (select id from exercicio where ano = 2018)
