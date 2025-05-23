update operadoratransporte opera
set opera.datacadastro = (select trunc(rev.datahora)
                            from operadoratransporte_aud aud
                                     inner join revisaoauditoria rev on rev.id = aud.rev
                            where aud.revtype = 0 and opera.id = aud.id
)
where opera.datacadastro is null
