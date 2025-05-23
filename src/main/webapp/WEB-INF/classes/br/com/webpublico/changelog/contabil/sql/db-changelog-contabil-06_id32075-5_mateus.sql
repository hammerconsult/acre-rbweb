merge into ajusteativodisponivel ajuste using (
  select ad.id as ajusteId, cd.id as contaId
  from ajusteativodisponivel ad
    inner join FONTEDERECURSOS fr on ad.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) ajd on (ajd.ajusteId = ajuste.id)
when matched then update set ajuste.contadedestinacao_id = ajd.contaId
