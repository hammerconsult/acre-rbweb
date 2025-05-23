merge into TRANSPORTESALDOFINANCEIRO transporte using (
  select t.id as transpId, cd.id as contaId
  from TRANSPORTESALDOFINANCEIRO t
    inner join FONTEDERECURSOS fr on t.FONTE_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) transp on (transp.transpId = transporte.id)
when matched then update set transporte.contadedestinacao_id = transp.contaId
