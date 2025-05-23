update processoparcelamento parc set parc.USUARIORESPONSAVEL_ID =
(select usu.id
from processoparcelamento pp
inner join processoparcelamento_aud aud on aud.id = pp.id
inner join REVISAOAUDITORIA rev on rev.id = aud.rev
inner join USUARIOSISTEMA usu on usu.login = rev.USUARIO
where pp.USUARIORESPONSAVEL_ID is null
  and pp.id = parc.id
  and rev.DATAHORA = (select min(r.datahora) from REVISAOAUDITORIA r inner join processoparcelamento_aud a on a.rev = r.id where a.id = pp.id and r.usuario <> 'WebPúblico (Manipulação Interna)')
)
where
(select usu.id
from processoparcelamento pp
inner join processoparcelamento_aud aud on aud.id = pp.id
inner join REVISAOAUDITORIA rev on rev.id = aud.rev
inner join USUARIOSISTEMA usu on usu.login = rev.USUARIO
where pp.USUARIORESPONSAVEL_ID is null
  and pp.id = parc.id
  and rev.DATAHORA = (select min(r.datahora) from REVISAOAUDITORIA r inner join processoparcelamento_aud a on a.rev = r.id where a.id = pp.id and r.usuario <> 'WebPúblico (Manipulação Interna)')
) is not null
