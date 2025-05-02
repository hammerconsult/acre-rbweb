update subcontadividapublica subContaDiv set subContaDiv.tipovalidacao = 'DESPESA_ORCAMENTARIA' ,subContaDiv.fontederecursos_id =
(select fr.id from fontederecursos fr where fr.exercicio_id = subContaDiv.exercicio_id and fr.codigo = '01')
where subContaDiv.id in (select sub.id from dividapublica div
inner join categoriadividapublica cat on DIV.CATEGORIADIVIDAPUBLICA_ID = cat.id
inner join SUBCONTADIVIDAPUBLICA sub on div.id = SUB.DIVIDAPUBLICA_ID
where cat.naturezadividapublica not in ('OPERACAO_CREDITO', 'PRECATORIO')
and subContaDiv.exercicio_id = SUB.EXERCICIO_ID
)
