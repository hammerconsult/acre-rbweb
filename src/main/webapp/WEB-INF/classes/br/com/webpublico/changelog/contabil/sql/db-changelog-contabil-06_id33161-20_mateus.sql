merge into FONTEFECHAMENTOEXERCICIO fonte using (
  select ffe.id as fontefecid, cd.id as contaId
  from FONTEFECHAMENTOEXERCICIO ffe
    inner join FONTEDERECURSOS fr on ffe.FONTEDERECURSOS_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) fontef on (fontef.fontefecid = fonte.id)
when matched then update set fonte.contadedestinacao_id = fontef.contaId
