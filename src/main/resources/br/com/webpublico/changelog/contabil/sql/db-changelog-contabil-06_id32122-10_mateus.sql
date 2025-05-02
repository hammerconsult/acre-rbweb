merge into TRANSPORTEOBRIGACAOAPAGAR obrigacao using (
    select ob.id as obrId, cd.id as contaId
    from TRANSPORTEOBRIGACAOAPAGAR ob
      inner join FONTEDERECURSOS fr on ob.fonteDeRecursos_ID = fr.id
      inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
      inner join conta c on cd.id = c.id
    where cd.DETALHAMENTOFONTEREC_ID is null
  ) obr on (obr.obrId = obrigacao.id)
when matched then update set obrigacao.contadedestinacao_id = obr.contaId
