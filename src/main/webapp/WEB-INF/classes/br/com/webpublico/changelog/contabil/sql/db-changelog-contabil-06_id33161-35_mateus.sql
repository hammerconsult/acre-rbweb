merge into ReceitaAlteracaoORC receita using (
  select rec.id as receitaid, cd.id as contaId
  from ReceitaAlteracaoORC rec
    inner join FONTEDERECURSOS fr on rec.FONTEDERECURSO_ID = fr.id
    inner join contadedestinacao cd on fr.id = cd.FONTEDERECURSOS_ID
    inner join conta c on cd.id = c.id
  where cd.DETALHAMENTOFONTEREC_ID is null
) re on (re.receitaid = receita.id)
when matched then update set receita.contadedestinacao_id = re.contaId
