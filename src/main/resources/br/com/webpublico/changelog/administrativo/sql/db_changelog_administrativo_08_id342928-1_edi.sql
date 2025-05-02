
update itemsaidamaterial ism
set ism.material_id = (select mov.material_id
                       from movimentoestoque mov
                       where mov.id = ism.movimentoestoque_id)
where ism.material_id is null
  and ism.id = (select item.id
                from saidamaterial sm
                         inner join itemsaidamaterial item on sm.id = item.saidamaterial_id
                where ism.id = item.id
                  and sm.situacaosaidamaterial = 'CONCLUIDA');
