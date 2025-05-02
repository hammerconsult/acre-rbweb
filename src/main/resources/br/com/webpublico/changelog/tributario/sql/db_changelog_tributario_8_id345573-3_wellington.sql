declare
v_sequencial integer;
begin
for solicitacao in (select id from solicitacaoalvaraimediato)
    loop
        v_sequencial := 1;
for exigencia in  (select id from EXIGENCIAALVARAIMEDIATO
                           where SOLICITACAO_ID = solicitacao.ID
                           order by id)
        loop
update EXIGENCIAALVARAIMEDIATO set dataregistro =  (select max(rev.datahora)
                                                    from EXIGENCIAALVARAIMEDIATO_aud a
                                                             inner join revisaoauditoria rev on rev.id = a.rev
                                                    where a.id = exigencia.id),
                                   sequencial = v_sequencial
where id = exigencia.id;
v_sequencial := v_sequencial + 1;
end loop;
end loop;
end;
