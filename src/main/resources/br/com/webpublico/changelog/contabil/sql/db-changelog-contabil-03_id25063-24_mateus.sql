update saldodividapublica saldo set saldo.fontederecursos_id =
(select fr.id from fontederecursos fr where fr.exercicio_id = (select id from exercicio where ano = extract(year from TRUNC(data))) and fr.codigo = '01')
where SALDO.DIVIDAPUBLICA_ID in (select div.id from dividapublica div
inner join categoriadividapublica cat on DIV.CATEGORIADIVIDAPUBLICA_ID = cat.id
where cat.naturezadividapublica = 'PRECATORIO'
)
