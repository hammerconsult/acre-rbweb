begin
    for registro in (select id, cnpj from eventoredesim where situacao = 'PROCESSADO')
        loop
            update eventoredesim
            set inscricaocadastral = (select max(ce.inscricaocadastral)
                                      from cadastroeconomico ce
                                               inner join pessoajuridica pj on pj.id = ce.pessoa_id
                                      where replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') =
                                            replace(replace(replace(eventoredesim.cnpj, '.', ''), '-', ''), '/', ''))
            where id =
                  registro.id;
        end loop;
end;
