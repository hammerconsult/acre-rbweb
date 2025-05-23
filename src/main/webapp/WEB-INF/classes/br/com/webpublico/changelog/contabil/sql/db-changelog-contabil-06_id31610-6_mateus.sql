update programappa ap set datacadastro = (
select trunc(rev.datahora) from programappa_aud aud
inner join revisaoauditoria rev on rev.id = aud.rev
where aud.revtype = 0
and ap.id = aud.id)
