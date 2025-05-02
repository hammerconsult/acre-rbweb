    update processodecompra pc set pc.solicitacaomaterial_id = (
    select solicitacaomaterial.id
       from processodecompra
      inner join loteprocessodecompra on loteprocessodecompra.processodecompra_id = processodecompra.id
      inner join itemprocessodecompra on itemprocessodecompra.loteprocessodecompra_id = loteprocessodecompra.id
      inner join itemsolicitacao on itemprocessodecompra.itemsolicitacaomaterial_id = itemsolicitacao.id
      inner join lotesolicitacaomaterial on itemsolicitacao.lotesolicitacaomaterial_id = lotesolicitacaomaterial.id
      inner join solicitacaomaterial on lotesolicitacaomaterial.solicitacaomaterial_id = solicitacaomaterial.id
    where processodecompra.id = pc.id and rownum = 1)
