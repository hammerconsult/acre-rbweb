merge into saldodividapublica sdp using (
  select sld.id as sdpId, cd.id as contaId
  from saldodividapublica sld
    inner join FONTEDERECURSOS fr on sld.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) saldo on (saldo.sdpId = sdp.id)
when matched then update set sdp.contadedestinacao_id = saldo.contaId
