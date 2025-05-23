merge into enderecocadastroeconomico ece
    using (select ce.id             as idcmc,
                  ec.numero         as numero,
                  ec.complemento    as complemento,
                  ec.cep            as cep,
                  ec.bairro         as bairro,
                  ec.logradouro     as logradouro,
                  sece.tipoendereco as tipoenderecoece,
                  ec.localidade     as localidade,
                  ec.uf             as uf
           from cadastroeconomico ce
                    inner join pessoa pe on ce.pessoa_id = pe.id
                    inner join enderecocorreio ec on pe.enderecoprincipal_id = ec.id
                    left join enderecocadastroeconomico sece on ce.id = sece.cadastroeconomico_id
           where sece.tipoendereco is null
              or sece.tipoendereco = 'COMERCIAL') dados
    on (dados.idcmc = ece.cadastroeconomico_id)
    when matched then
        update
            set ece.numero       = dados.numero,
                ece.cep          = dados.cep,
                ece.complemento  = dados.complemento,
                ece.tipoendereco = 'COMERCIAL',
                ece.logradouro   = dados.localidade,
                ece.bairro       = dados.bairro,
                ece.localidade   = dados.localidade,
                ece.uf           = dados.uf
    when not matched then
        insert (id, cadastroeconomico_id, numero, complemento, cep, tipoendereco, logradouro, bairro, localidade, uf,
                areautilizacao, sequencial)
            values (hibernate_sequence.nextval, dados.idcmc, dados.numero, dados.complemento, dados.cep, 'COMERCIAL',
                    dados.logradouro, dados.bairro, dados.localidade, dados.localidade, 0.0, null)
