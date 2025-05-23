merge into itemsolicitacao o
using (
    select
        ic.unidademedida_id,
        item.id
    from itemsolicitacao item
             inner join itemcotacao ic on ic.id = item.itemcotacao_id
    where ic.unidademedida_id is not null and item.unidademedida_id is null) item on (item.id = o.id)
when matched then update set o.unidadeMedida_id = item.unidademedida_id
