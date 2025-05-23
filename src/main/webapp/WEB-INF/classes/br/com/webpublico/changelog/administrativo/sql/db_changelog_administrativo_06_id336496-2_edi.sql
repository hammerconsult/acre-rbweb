update ITEMSOLICITACAOEXTERNO
set TIPOCONTROLE = 'QUANTIDADE'
where SOLICITACAOMATERIALEXTERNO_ID in
      (select sol.id from SOLICITACAOMATERIALEXT sol where sol.TIPOSOLICITACAOREGISTROPRECO = 'EXTERNA');


update ITEMSOLICITACAOEXTERNO
set TIPOCONTROLE = (select ic.tipocontrole
                    from ITEMCOTACAO ic
                             inner join itemsolicitacao item on item.ITEMCOTACAO_ID = ic.id
                             inner join itemsolicitacaomaterial ism on ism.ITEMSOLICITACAO_ID = item.id
                             inner join itemprocessodecompra ipc on ipc.ITEMSOLICITACAOMATERIAL_ID = item.id
                    where ipc.id = ITEMPROCESSOCOMPRA_ID)
where TIPOCONTROLE is null;


update ITEMSOLICITACAOEXTERNO
set TIPOCONTROLE = 'QUANTIDADE'
where TIPOCONTROLE is null;
