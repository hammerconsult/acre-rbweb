merge into DOTACAOFECHAMENTOEXERCICIO dotacao using (
  select dfe.id as dotacaoid, cd.id as contaId
  from DOTACAOFECHAMENTOEXERCICIO dfe
    inner join FONTEDERECURSOS fr on dfe.FONTEDERECURSOS_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) dotf on (dotf.dotacaoid = dotacao.id)
when matched then update set dotacao.contadedestinacao_id = dotf.contaId
