update itemrequisicaocompraest item
set item.itemrequisicaocompra_id = (
    select ire.ITEMREQUISICAOCOMPRA_ID
    from ITEMREQUISICAOCOMPRAEXEC ire
             inner join itemrequisicaocompraest itemEst on itemEst.ITEMREQUISICAOCOMPRAEXEC_ID = ire.id
    where item.id = itemest.id
);
