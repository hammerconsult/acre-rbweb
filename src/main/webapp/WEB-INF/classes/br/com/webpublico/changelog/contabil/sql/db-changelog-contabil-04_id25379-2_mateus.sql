update conta c set c.codigo = '1',  c.descricao = 'Recurso do Exercício Corrente'
where c.DTYPE = 'ContaDeDestinacao'
and c.codigo = '0'
and c.exercicio_id = (select id from exercicio where ano = 2018)
