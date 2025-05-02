merge into MOVIMENTODIARIODIVIDAPUB mdp using (
  select mv.id as mdpId, cd.id as contaId
  from MOVIMENTODIARIODIVIDAPUB mv
    inner join FONTEDERECURSOS fr on mv.fonteDeRecursos_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) mov on (mdp.id = mov.mdpId)
when matched then update set mdp.contadedestinacao_id = mov.contaId
