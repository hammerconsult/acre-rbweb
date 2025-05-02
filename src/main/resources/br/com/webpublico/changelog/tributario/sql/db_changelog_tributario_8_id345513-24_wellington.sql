declare
    v_proximo_sequencial numeric;
    v_count numeric;
begin
    for processos in (select pc.id as idProcesso,
                             a.id as idAlvara,
                             a.ENDERECOALVARA_ID as idEnderecoAlvara,
                             a.AREAOCUPADA as areaUtilizacao
                         from processocalculoalvara pc
                        left join alvara a on a.id = pc.ALVARA_ID)
    loop
        select count(1) into v_count
           from enderecocalculoalvara eca
        where eca.PROCESSOCALCULOALVARA_ID = processos.idProcesso
          and eca.tipoendereco = 'COMERCIAL';
        if (v_count = 0) then
            update ENDERECOCALCULOALVARA set TIPOENDERECO = 'COMERCIAL'
            where id = (select min(id) from enderecocalculoalvara where PROCESSOCALCULOALVARA_ID = processos.idProcesso);
            update ENDERECOCALCULOALVARA set TIPOENDERECO = 'CORRESPONDENCIA'
            where PROCESSOCALCULOALVARA_ID = processos.idProcesso
               and TIPOENDERECO is null;
        end if;

        update enderecocalculoalvara set sequencial = 0, areautilizacao = processos.areaUtilizacao
        where tipoendereco = 'COMERCIAL'
          and PROCESSOCALCULOALVARA_ID = processos.idProcesso;
        v_proximo_sequencial := 1;
        for enderecos in (select id from enderecocalculoalvara
                          where PROCESSOCALCULOALVARA_ID = processos.idProcesso
                            and TIPOENDERECO != 'COMERCIAL'
                          order by id)
        loop
            update ENDERECOCALCULOALVARA set sequencial = v_proximo_sequencial
            where id = enderecos.id;
            v_proximo_sequencial := v_proximo_sequencial + 1;
        end loop;
        if (processos.idEnderecoAlvara is not null) then
            update enderecoalvara set alvara_id = processos.idAlvara,
                                      sequencial = 0, tipoendereco = 'COMERCIAL',
                                      areautilizacao = processos.areaUtilizacao
            where id = processos.idEnderecoAlvara;
        end if;
        if (processos.idAlvara is not null) then
            update alvara set ENDERECOALVARA_ID = null
            where id = processos.idAlvara;
        end if;
    end loop;
end;

