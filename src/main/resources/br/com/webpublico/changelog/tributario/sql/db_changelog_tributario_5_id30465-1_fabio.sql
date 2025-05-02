merge into lote l using (

select lote.id, dados.certo from quadra qd
inner join lote lote on qd.id = lote.quadra_id
inner join
(select coalesce(DESCRICAO,'') descricao, SETOR_ID, codigo, count(*), min(id) certo
from quadra
group by coalesce(DESCRICAO,''), SETOR_ID, codigo
having count(*) > 1) dados on dados.descricao = coalesce(qd.descricao,'') and dados.setor_id = qd.setor_id and dados.codigo = qd.codigo
where qd.id <> dados.certo
and exists (select lt.id from lote lt where lt.quadra_id = qd.id)
) lote on (lote.id = l.id)

when matched then update set l.quadra_id = lote.certo
