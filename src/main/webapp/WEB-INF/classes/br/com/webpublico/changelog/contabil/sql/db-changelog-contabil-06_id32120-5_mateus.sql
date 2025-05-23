merge into SubContaFonteRec sc using (
  select subcontaf.id as scfrId, cd.id as contaId
  from SubContaFonteRec subcontaf
    inner join FONTEDERECURSOS fr on subcontaf.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) scfr on (scfr.scfrId = sc.id)
when matched then update set sc.contadedestinacao_id = scfr.contaId
