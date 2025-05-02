update AverbacaoTempoServico a set a.observacao =
(
    select ato.FUNDAMENTACAOLEGAL 
    from AverbacaoTempoServico ave
    inner join atolegal ato on ato.id = ave.ATOLEGAL_ID
    where a.id = ave.ID
)