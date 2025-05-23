update solicitacaoempenhoestorno see
set see.datasolicitacao =
        (select est.dataestorno
         from empenhoestorno est
                  inner join solicitacaoempenhoestorno sol on sol.empenhoestorno_id = est.id
         where sol.id = see.id)
where extract(year from see.DATASOLICITACAO) = 2023
  and see.EMPENHOESTORNO_ID is not null
