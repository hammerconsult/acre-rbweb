delete from quadra where id in (
select qd.id from quadra qd
inner join
(select coalesce(DESCRICAO,'') descricao, SETOR_ID, codigo, count(*), min(id) certo
from quadra
group by coalesce(DESCRICAO,''), SETOR_ID, codigo
having count(*) > 1) dados on dados.descricao = coalesce(qd.descricao,'') and dados.setor_id = qd.setor_id and dados.codigo = qd.codigo
where qd.id <> dados.certo)
