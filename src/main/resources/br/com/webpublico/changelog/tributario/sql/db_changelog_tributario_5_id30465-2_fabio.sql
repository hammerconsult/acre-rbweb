merge into facevalor fv using (
select face.id, dados.certo from quadra qd
inner join facevalor face on face.quadra_id = qd.id
inner join
(select coalesce(DESCRICAO,'') descricao, SETOR_ID, codigo, count(*), min(id) certo
from quadra
group by coalesce(DESCRICAO,''), SETOR_ID, codigo
having count(*) > 1) dados on dados.descricao = coalesce(qd.descricao,'') and dados.setor_id = qd.setor_id and dados.codigo = qd.codigo
where qd.id <> dados.certo
and exists (select fv.id from FaceValor fv where fv.quadra_id = qd.id)
) face on (face.id = fv.id)

when matched then update set fv.quadra_id = face.certo
