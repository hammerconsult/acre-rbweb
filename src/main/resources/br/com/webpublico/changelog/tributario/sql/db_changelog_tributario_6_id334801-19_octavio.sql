declare
    cursor log_alvara is select l.id, to_char(l.data, 'dd/MMyyyy') as data from logalvararedesim l;
    idrevisao number;
begin
    for log in log_alvara
        loop
            select max(revisao.id) into idrevisao
            from logalvararedesim_aud log_aud2
                     inner join revisaoauditoria revisao on revisao.id = log_aud2.rev
            where log_aud2.id = log.id
              and trunc(revisao.datahora) <= to_date(log.data, 'dd/MM/yyyy') and rownum = 1;

            update logalvararedesim set idalvara = (select calculo_aud.alvara_id
                                                    from processocalculoalvara_aud calculo_aud
                                                             inner join logalvararedesim_aud log_aud on log_aud.processocalculoalvara_id = calculo_aud.id
                                                    where calculo_aud.rev = idrevisao
                                                      and (calculo_aud.revtype <> 2 or calculo_aud.revtype is null)
                                                    group by calculo_aud.alvara_id)
            where idalvara is null and id = log.id;
        end loop;
end;
