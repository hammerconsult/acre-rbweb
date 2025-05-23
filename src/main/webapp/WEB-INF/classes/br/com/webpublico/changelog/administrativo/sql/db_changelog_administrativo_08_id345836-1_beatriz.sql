insert into reducaovalorbemresidual (id, lotereducaovalorbem_id, grupobem_id, bem_id, valororiginal, valorliquido)
select hibernate_sequence.nextval,
       item.lotereducaovalorbem_id,
       estres.grupobem_id,
       bm.id,
       estres.valororiginal,
       estres.valororiginal - estres.valoracumuladodadepreciacao
from bem bm
         inner join eventobem ev on ev.bem_id = bm.id
         inner join estadobem estres on estres.id = ev.estadoresultante_id
         inner join reducaovalorbem item on item.id = ev.id
where ev.tipoeventobem = 'DEPRECIACAO'
  and ev.valordolancamento = 0;
