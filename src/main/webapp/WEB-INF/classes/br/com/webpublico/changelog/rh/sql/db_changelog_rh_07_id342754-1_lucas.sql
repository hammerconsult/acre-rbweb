update EVENTOFP e
set e.FORMULAVALORINTEGRAL = e.FORMULA
where e.id in (select ev.id
               from ItemBaseFP item
                        inner join baseFP base on item.BASEFP_ID = base.ID
                        inner join eventoFP ev on item.EVENTOFP_ID = ev.ID
               where base.codigo = '1004'
                 and ev.FORMULAVALORINTEGRAL like 'return 0;'
                 and ev.FORMULA not like 'return 0;'
                 and ev.ATIVO = 1);
