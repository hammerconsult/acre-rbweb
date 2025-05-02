merge into ITEMSOLICITACAOEXTERNO item
    using (select ipc.NUMERO as numero_item,
                  ism.id     as id_item,
                  ipc.id     as id_item_processo
           from ITEMSOLICITACAOEXTERNO ism
                    inner join itemprocessodecompra ipc on ipc.id = ism.ITEMPROCESSOCOMPRA_ID) dados
    on (dados.id_item = item.ID and dados.id_item_processo = item.ITEMPROCESSOCOMPRA_ID)
    when matched then
        update
            set item.NUMERO = dados.numero_item;
