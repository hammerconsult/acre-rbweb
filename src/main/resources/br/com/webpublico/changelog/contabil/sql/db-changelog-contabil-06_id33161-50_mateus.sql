merge into TRANSPORTEEXTRA transp using (
  select t.id as transporteId, cd.id as contaId
  from TRANSPORTEEXTRA t
    inner join FONTEDERECURSOS fr on t.fonte_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) trp on (trp.transporteId = transp.id)
when matched then update set transp.contadedestinacao_id = trp.contaId
