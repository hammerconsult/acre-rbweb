update movimentodividapublica mov set mov.fontederecursos_id =
(select fr.id from fontederecursos fr where fr.exercicio_id = mov.exercicio_id and fr.codigo = '01')
where mov.id in (select sub.id from dividapublica div
inner join categoriadividapublica cat on DIV.CATEGORIADIVIDAPUBLICA_ID = cat.id
inner join movimentodividapublica sub on div.id = SUB.DIVIDAPUBLICA_ID
where cat.naturezadividapublica not in ('OPERACAO_CREDITO', 'PRECATORIO')
and mov.exercicio_id = SUB.EXERCICIO_ID
)
