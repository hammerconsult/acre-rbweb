update solicitacaoempenho se set se.execucaocontratoempenho_id = (select id from execucaocontratoempenho ece where ece.solicitacaoempenho_id = se.id)
