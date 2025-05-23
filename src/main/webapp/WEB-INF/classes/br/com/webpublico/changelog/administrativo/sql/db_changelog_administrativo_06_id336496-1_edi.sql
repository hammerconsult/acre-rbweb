merge into ITEMSOLICITACAOEXTERNO it
using ( select item.id as id,
               item.SOLICITACAOMATERIALEXTERNO_ID as id_solicitacao,
               (ROW_NUMBER() OVER (PARTITION BY item.SOLICITACAOMATERIALEXTERNO_ID ORDER BY item.id) * 1) as numero_item
        from ITEMSOLICITACAOEXTERNO item
) i on (i.id = it.id)
when matched then update set NUMERO = i.numero_item where SOLICITACAOMATERIALEXTERNO_ID = i.id_solicitacao;
