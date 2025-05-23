merge into TRANSFERENCIACONTAFINANC tcf using (
  select t.id as scfrId, cd.id as contaId
  from TRANSFERENCIACONTAFINANC t
    inner join FONTEDERECURSOS fr on t.fonteDeRecursosRetirada_id = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) transf on (transf.scfrId = tcf.id)
when matched then update set tcf.contaDeDestinacaoRetirada_id = transf.contaId
