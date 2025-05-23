merge into SolicitacaoEmpenhoEstorno solicitacao using (
    select est.id as idEmpenhoEstorno, solest.id as idSolicitacaoSemEmpenhoEstorno
    from SolicitacaoEmpenhoEstorno solest
             inner join empenho emp on solest.EMPENHO_ID = emp.ID
             inner join EMPENHOESTORNO est on emp.ID = est.EMPENHO_ID
             inner join solicitacaoempenho sol on solest.SOLICITACAOEMPENHO_ID = sol.ID
    where solest.EMPENHOESTORNO_ID is null
) dados on (dados.idSolicitacaoSemEmpenhoEstorno = solicitacao.id)
    when matched then update set solicitacao.EMPENHOESTORNO_ID = dados.idEmpenhoEstorno
