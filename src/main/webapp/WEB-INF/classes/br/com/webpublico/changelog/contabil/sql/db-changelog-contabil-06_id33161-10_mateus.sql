merge into RECEITAEXTFECHAMENTOEXERC receita using (
  select rfe.id as receitaid, cd.id as contaId
  from RECEITAEXTFECHAMENTOEXERC rfe
    inner join FONTEDERECURSOS fr on rfe.FONTEDERECURSOS_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) recf on (recf.receitaid = receita.id)
when matched then update set receita.contadedestinacao_id = recf.contaId
