merge into itemcotacao o
using (
    select
        ob.unidademedida_id,
        item.id
    from itemcotacao item
             inner join objetocompra ob on ob.id = item.objetocompra_id
    where ob.unidademedida_id is not null and item.unidademedida_id is null) item on (item.id = o.id)
when matched then update set o.unidadeMedida_id = item.unidademedida_id
