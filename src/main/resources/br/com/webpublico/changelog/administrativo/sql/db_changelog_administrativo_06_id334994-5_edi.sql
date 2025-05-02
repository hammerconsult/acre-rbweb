merge into itemrequisicaocompraest item using (
    select itemEst.id,
           (select irce.id from ITEMREQUISICAOCOMPRAEXEC irce
            inner join itemrequisicaodecompra irc on irc.id = irce.ITEMREQUISICAOCOMPRA_ID
            where irce.ITEMREQUISICAOCOMPRA_ID = itemest.ITEMREQUISICAODECOMPRA_ID
              and irc.REQUISICAODECOMPRA_ID = est.REQUISICAODECOMPRA_ID) as id_itemExecucao
    from REQUISICAOCOMPRAESTORNO est
             inner join itemrequisicaocompraest itemEst on itemEst.REQUISICAOCOMPRAESTORNO_ID = est.id
) dados on (dados.id = item.id)
when matched then update set item.ITEMREQUISICAOCOMPRAEXEC_ID = dados.id_itemExecucao;
