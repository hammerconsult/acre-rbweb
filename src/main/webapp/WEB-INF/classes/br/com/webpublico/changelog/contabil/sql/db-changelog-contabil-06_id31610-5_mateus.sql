update acaoppa ap set datacadastro = (
select trunc(rev.datahora) from acaoppa_aud aud
inner join revisaoauditoria rev on rev.id = aud.rev
where aud.revtype = 0
and ap.id = aud.id)
