merge into MOVIMENTODIVIDAPUBLICA mdp using (
  select mov.id as movId, cd.id as contaId
  from MOVIMENTODIVIDAPUBLICA mov
    inner join FONTEDERECURSOS fr on mov.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) mov on (mov.movId = mdp.id)
when matched then update set mdp.contadedestinacao_id = mov.contaId
