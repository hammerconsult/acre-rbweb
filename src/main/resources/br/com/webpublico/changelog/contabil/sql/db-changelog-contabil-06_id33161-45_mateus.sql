merge into SALDOEXTRAORCAMENTARIO saldo using (
  select sld.id as saldoId, cd.id as contaId
  from SALDOEXTRAORCAMENTARIO sld
    inner join FONTEDERECURSOS fr on sld.FONTEDERECURSOS_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) s on (s.saldoId = saldo.id)
when matched then update set saldo.contadedestinacao_id = s.contaId
