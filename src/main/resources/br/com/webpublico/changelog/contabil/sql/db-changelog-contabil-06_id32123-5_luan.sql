merge into LIBERACAOCOTAFINANCEIRA lcf using (
  select libCFin.id as lcfFrId, cd.id as contaId
  from LIBERACAOCOTAFINANCEIRA libCFin
    inner join FONTEDERECURSOS fr on libCFin.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) lcfFr on (lcfFr.lcfFrId = lcf.id)
when matched then update set lcf.contadedestinacao_id = lcfFr.contaId
