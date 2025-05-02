update veiculoottransporte veiculo
set veiculo.datacadastro = (select trunc(rev.datahora)
                            from veiculoottransporte_aud aud
                            inner join revisaoauditoria rev on rev.id = aud.rev
                            where aud.revtype = 0 and veiculo.id = aud.id
)
where veiculo.datacadastro is null
