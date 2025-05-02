update itemsolicitacao item set item.objetocompra_id = (
    select objetocompra_id from itemsolicitacaomaterial where itemsolicitacao_id = item.id);
