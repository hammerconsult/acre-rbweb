merge into PAGAMENTOEXTRA pe using (
  select pagExtra.id as pefrId, cd.id as contaId
  from PAGAMENTOEXTRA pagExtra
    inner join FONTEDERECURSOS fr on pagExtra.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) pefr on (pefr.pefrId = pe.id)
when matched then update set pe.contadedestinacao_id = pefr.contaId
