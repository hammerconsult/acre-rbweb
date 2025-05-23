update dotacaosolmatitemfonte f
set f.gerareservaorc = (select dsm.integroucontabil
                        from dotsolmat dsm
                                 inner join dotacaosolmatitem item on item.dotacaosolicitacaomaterial_id = dsm.id
                                 inner join dotacaosolmatitemfonte fonte on fonte.dotacaosolmatitem_id = item.id
                        where fonte.id = f.id)
where f.gerareservaorc is null;

update dotacaosolmatitemfonte set gerareservaorc = 0 where gerareservaorc is null;
