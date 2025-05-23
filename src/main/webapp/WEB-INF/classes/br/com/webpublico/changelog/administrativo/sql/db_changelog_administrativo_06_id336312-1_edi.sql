update itemsolicitacaoexterno item
set item.subtipoobjetocompra = (select sol.subtipoobjetocompra
                                from solicitacaomaterialext sol
                                where sol.id = item.solicitacaomaterialexterno_id
                                  and sol.subtipoobjetocompra is not null)
