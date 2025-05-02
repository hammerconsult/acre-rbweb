create or replace view vwrhfichafinanceira as 
select ficha.vinculofp_id as id_vinculo, 
       folha.ano, 
       folha.mes, 
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item where item.tipoeventofp = 'VANTAGEM' and item.fichafinanceirafp_id = ficha.id ) as vantagem,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item where item.tipoeventofp = 'DESCONTO' and item.fichafinanceirafp_id = ficha.id ) as desconto,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item join eventofp evento on evento.id = item.eventofp_id
                               where evento.codigo in ('151', '294') and item.fichafinanceirafp_id = ficha.id) as um_terco_ferias
from fichafinanceirafp ficha 
        join folhadepagamento folha on ficha.folhadepagamento_id = folha.id
            order by folha.ano desc, folha.mes