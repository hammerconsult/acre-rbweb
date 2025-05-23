update movimentodividapublica mov set mov.fontederecursos_id =
(select fr.id from fontederecursos fr where fr.exercicio_id = mov.exercicio_id and fr.codigo = '08')
where mov.id in (select sub.id from dividapublica div
inner join categoriadividapublica cat on DIV.CATEGORIADIVIDAPUBLICA_ID = cat.id
inner join movimentodividapublica sub on div.id = SUB.DIVIDAPUBLICA_ID
where cat.naturezadividapublica = 'OPERACAO_CREDITO'
and mov.exercicio_id = SUB.EXERCICIO_ID
)
