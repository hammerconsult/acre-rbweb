update creditoreceber div set div.CONTADEDESTINACAO_ID = (select cd.id from fontederecursos fr
inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
inner join conta c on cd.id = c.id
where fr.codigo like '%01'
and cd.DETALHAMENTOFONTEREC_ID is null
and fr.EXERCICIO_ID = div.exercicio_id)
