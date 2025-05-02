declare
begin
for eventos in (select ev.id
                    from bem bm
                             inner join eventobem ev on ev.bem_id = bm.id
                             inner join estadobem estres on estres.id = ev.estadoresultante_id
                             inner join reducaovalorbem item on item.id = ev.id
                    where ev.tipoeventobem = 'DEPRECIACAO'
                      and ev.valordolancamento = 0
                      and item.id not in (select itemest.reducaovalorbem_id
                                          from estornoreducaovalorbem itemest
                                                   inner join reducaovalorbem red on red.id = itemest.reducaovalorbem_id
                                          where red.id = item.id))
        loop
delete
from reducaovalorbem
where id = eventos.id;
delete
from eventobem
where id = eventos.id;
end loop;
end;
