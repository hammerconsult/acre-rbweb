merge into DESPESAFECHAMENTOEXERCICIO despesa using (
  select dfe.id as dotacaoid, cd.id as contaId
  from DESPESAFECHAMENTOEXERCICIO dfe
    inner join FONTEDERECURSOS fr on dfe.FONTEDERECURSOS_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) despf on (despf.dotacaoid = despesa.id)
when matched then update set despesa.contadedestinacao_id = despf.contaId
