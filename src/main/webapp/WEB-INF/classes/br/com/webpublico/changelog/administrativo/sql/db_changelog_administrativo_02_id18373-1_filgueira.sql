update CONFIGURACAOLICITACAO conf set conf.QUANTIDADEMINIMACOTACAO = null;
update CONFIGURACAOLICITACAO_AUD conf set conf.QUANTIDADEMINIMACOTACAO = null;
update COTACAO cotacao set cotacao.justificativa  = null;
update COTACAO_AUD cotacao set cotacao.justificativa  = null;
update ITEMCOTACAO item set item.justificativa = null;
update ITEMCOTACAO_AUD item set item.justificativa = null;