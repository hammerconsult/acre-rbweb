update ITEMSOLICITACAOEXTERNO item set item.UNIDADEMEDIDA_ID = (
    select ism.UNIDADEMEDIDA_ID from ITEMSOLICITACAO ism
    inner join itemprocessodecompra ipc on ipc.ITEMSOLICITACAOMATERIAL_ID = ism.id
    where ipc.id = item.ITEMPROCESSOCOMPRA_ID
);

update ITEMSOLICITACAOEXTERNO item set item.UNIDADEMEDIDA_ID = 98764445
where item.UNIDADEMEDIDA_ID is null;
