merge into itemdemonstrativo item using (
select cfg.ordem, cfg.grupo, cfg.coluna, cfg.espaco, cfg.RELATORIOSITEMDEMONST_ID, it.id from itemdemonstrativo it
inner join CONFIGITEMDEMONSTRELATORIO cfg on cfg.itemdemonstrativo_id = it.id
inner join relatoriositemdemonst rel on cfg.RELATORIOSITEMDEMONST_ID = rel.id
inner join exercicio ex on rel.EXERCICIO_ID = ex.id
where ex.ano >= 2014
) itens on (itens.id = item.id)
when matched then update set item.ordem = itens.ordem,
                             item.grupo = itens.grupo,
                             item.coluna = itens.coluna,
                             item.espaco = itens.espaco,
                             item.RELATORIOSITEMDEMONST_ID = itens.RELATORIOSITEMDEMONST_ID
