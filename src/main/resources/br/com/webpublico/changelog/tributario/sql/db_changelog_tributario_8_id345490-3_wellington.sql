begin
for cnpjs in (select pj.cnpj
                  from cadastroeconomico ce
                           inner join pessoajuridica pj on pj.id = ce.pessoa_id
                  where VALIDA_CPF_CNPJ(pj.cnpj) = 'S'
                  group by pj.cnpj
                  having count(1) > 1)
        loop
            for cmc_ativos in (select ce.id, ce.INSCRICAOCADASTRAL, s.SITUACAOCADASTRAL
                               from cadastroeconomico ce
                                        inner join pessoajuridica pj on pj.id = ce.pessoa_id
                                        inner join situacaocadastroeconomico s on s.id = (select max(ce_s.situacaocadastroeconomico_id)
                                                                                          from ce_situacaocadastral ce_s
                                                                                          where ce_s.cadastroeconomico_id = ce.id)
                               where s.situacaocadastral = 'ATIVO'
                                 and pj.cnpj = cnpjs.cnpj)
            loop
delete from HISTORICOINSCRICAOCADASTRAL where CADASTRO_ID = cmc_ativos.id;

insert into historicoinscricaocadastral (id, dataregistro, cadastro_id, inscricaocadastral, situacaocadastral)
values (HIBERNATE_SEQUENCE.nextval, current_timestamp, cmc_ativos.id, cmc_ativos.INSCRICAOCADASTRAL,
        cmc_ativos.SITUACAOCADASTRAL);

for cmc_inativos in (select ce.id, ce.INSCRICAOCADASTRAL, s.SITUACAOCADASTRAL
                                        from cadastroeconomico ce
                                       inner join pessoajuridica pj on pj.id = ce.pessoa_id
                                       inner join situacaocadastroeconomico s on s.id = (select max(ce_s.situacaocadastroeconomico_id)
                                                                                              from ce_situacaocadastral ce_s
                                                                                              where ce_s.cadastroeconomico_id = ce.id)
                                   where s.situacaocadastral != 'ATIVO'
                                     and pj.cnpj = cnpjs.cnpj)
                loop
                    insert into historicoinscricaocadastral (id, dataregistro, cadastro_id, inscricaocadastral, situacaocadastral)
                    values (HIBERNATE_SEQUENCE.nextval, current_timestamp, cmc_ativos.id, cmc_inativos.INSCRICAOCADASTRAL,
                            cmc_inativos.SITUACAOCADASTRAL);
end loop;

update cadastroeconomico set inscricaocadastral = cnpjs.cnpj where id = cmc_ativos.id;
end loop;
end loop;
end;
