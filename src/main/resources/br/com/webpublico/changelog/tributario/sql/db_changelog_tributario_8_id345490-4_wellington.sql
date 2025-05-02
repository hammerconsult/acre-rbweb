    begin
    for cmc in (select ce.id, ce.inscricaocadastral, pj.cnpj, s.SITUACAOCADASTRAL
                         from cadastroeconomico ce
                        inner join pessoajuridica pj on pj.id = ce.pessoa_id
                        inner join situacaocadastroeconomico s on s.id = (select max(ce_s.situacaocadastroeconomico_id)
                                                                          from ce_situacaocadastral ce_s
                                                                          where ce_s.cadastroeconomico_id = ce.id)
                      where not exists (select 1 from historicoinscricaocadastral h where h.cadastro_id = ce.id))
            loop
                insert into historicoinscricaocadastral (id, dataregistro, cadastro_id, inscricaocadastral, situacaocadastral)
                values (HIBERNATE_SEQUENCE.nextval, current_timestamp, cmc.id, cmc.INSCRICAOCADASTRAL,
                        cmc.SITUACAOCADASTRAL);

    update cadastroeconomico set inscricaocadastral = cmc.cnpj where id = cmc.id;
    end loop;
    end;
