update itemdoctoitementrada
set valorliquidado = 0,
    SITUACAO       = 'AGUARDANDO_LIQUIDACAO'
where id in (Select associacao.id
             From itementradamaterial item
                      Inner Join itemdoctoitementrada associacao On associacao.itementradamaterial_id = item.id
                      Inner Join Itemdoctofiscalliquidacao itemDocFisc
                                 On itemDocFisc.id = associacao.itemdoctofiscalliquidacao_id
                      Inner Join doctofiscalliquidacao doc On doc.Id = itemDocFisc.doctofiscalliquidacao_id
                      inner join detentordocto dd on dd.DOCTOFISCALLIQUIDACAO_ID = doc.id
             where dd.SITUACAO = 'AGUARDANDO_LIQUIDACAO');


update ITEMDOCTOITEMENTRADA itemdoc
set itemdoc.SITUACAO       = 'LIQUIDADO',
    itemdoc.VALORLIQUIDADO = (Select item.VALORTOTAL
                              From itementradamaterial item
                                       Inner Join itemdoctoitementrada associacao
                                                  On associacao.itementradamaterial_id = item.id
                                       Inner Join Itemdoctofiscalliquidacao itemDocFisc
                                                  On itemDocFisc.id = associacao.itemdoctofiscalliquidacao_id
                                       Inner Join doctofiscalliquidacao doc
                                                  On doc.Id = itemDocFisc.doctofiscalliquidacao_id
                                       inner join material mat on mat.id = item.material_id
                                       inner join detentordocto dd on dd.DOCTOFISCALLIQUIDACAO_ID = doc.id
                              where dd.SITUACAO = 'LIQUIDADO'
                                and associacao.id = itemdoc.id)
where itemdoc.id in (Select associacao.id
                     From itementradamaterial item
                              Inner Join itemdoctoitementrada associacao On associacao.itementradamaterial_id = item.id
                              Inner Join Itemdoctofiscalliquidacao itemDocFisc
                                         On itemDocFisc.id = associacao.itemdoctofiscalliquidacao_id
                              Inner Join doctofiscalliquidacao doc On doc.Id = itemDocFisc.doctofiscalliquidacao_id
                              inner join material mat on mat.id = item.material_id
                              inner join detentordocto dd on dd.DOCTOFISCALLIQUIDACAO_ID = doc.id
                     where dd.SITUACAO = 'LIQUIDADO'
                       and associacao.id = itemdoc.id);
