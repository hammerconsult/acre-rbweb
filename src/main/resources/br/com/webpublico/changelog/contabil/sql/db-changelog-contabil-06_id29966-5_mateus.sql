update MEDICAOPROVISAOPPA med set med.usuariosistema_id = (
select usu.id from MEDICAOPROVISAOPPA_aud aud
inner join revisaoauditoria rev on aud.rev = rev.id
inner join usuariosistema usu on rev.usuario = usu.login
where aud.revtype = 0
and med.id = aud.id)
