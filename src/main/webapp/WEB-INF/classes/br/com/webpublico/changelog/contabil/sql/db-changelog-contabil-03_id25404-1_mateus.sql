update fontederecursos fr set fr.codigo = '1' || fr.codigo
where fr.exercicio_id = (select id from exercicio where ano = 2018)
