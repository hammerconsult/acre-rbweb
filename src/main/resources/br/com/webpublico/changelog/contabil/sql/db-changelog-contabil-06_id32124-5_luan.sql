merge into SOLICITACAOCOTAFINANCEIRA scf using (
  select solFin.id as scFfrId, cd.id as contaId
  from SOLICITACAOCOTAFINANCEIRA solFin
    inner join FONTEDERECURSOS fr on solFin.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) scFfr on (scFfr.scFfrId = scf.id)
when matched then update set scf.contadedestinacao_id = scFfr.contaId
