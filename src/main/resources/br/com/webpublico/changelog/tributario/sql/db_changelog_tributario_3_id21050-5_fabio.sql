update LAUDOAVALIACAOITBI laudo set laudo.USUARIOIMPRESSAOLAUDO_ID =
(select usu.id from LAUDOAVALIACAOITBI_aud aud
inner join REVISAOAUDITORIA rev on rev.id = aud.rev
inner join usuariosistema usu on usu.login = rev.USUARIO
where aud.REVTYPE = 0
  and aud.id = laudo.id)
where laudo.USUARIOIMPRESSAOLAUDO_ID is null
