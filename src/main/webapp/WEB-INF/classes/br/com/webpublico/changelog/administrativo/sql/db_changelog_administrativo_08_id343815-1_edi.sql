update solicitacaoempenhoestorno solest set solest.solicitacaoempenho_id = (
    select sol.id from solicitacaoempenho sol  where sol.empenho_id = solest.empenho_id);
