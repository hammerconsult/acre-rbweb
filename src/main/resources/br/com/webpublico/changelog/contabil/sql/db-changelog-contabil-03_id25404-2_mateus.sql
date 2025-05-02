update fontederecursos fr set fr.codigoTCE = '1' || fr.codigoTCE
where fr.exercicio_id = (select id from exercicio where ano = 2018)
