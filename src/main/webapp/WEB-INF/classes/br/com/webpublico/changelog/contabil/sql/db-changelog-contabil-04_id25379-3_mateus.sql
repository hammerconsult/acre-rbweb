update conta c set c.codigo = '1.'||substr(c.codigo, 3, length(c.codigo))
where c.DTYPE = 'ContaDeDestinacao'
and c.exercicio_id = (select id from exercicio where ano = 2018)
and c.codigo not in ('1')
