update solicitacaomaterial set tipoapuracao = replace(tipoapuracao, 'PRECO_', '');

update licitacao set tipoapuracao = replace(tipoapuracao, 'PRECO_', '');

update formulariocotacao fc set fc.tipoapuracaolicitacao =
(select distinct sm.tipoapuracao
     from cotacao c
    inner join solicitacaomaterial sm on sm.cotacao_id = c.id
  where fc.id = c.formulariocotacao_id)

