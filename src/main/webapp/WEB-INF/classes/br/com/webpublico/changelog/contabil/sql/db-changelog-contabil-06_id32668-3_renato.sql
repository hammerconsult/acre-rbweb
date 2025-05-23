update CALAMIDADEPUBLICABEMSERV set pessoa = (
select coalesce(pf.nome, pj.razaosocial) from pessoa p
left join pessoafisica pf on p.id = pf.id
left join pessoajuridica pj on p.id = pj.id
where pessoa_id = p.id
)
