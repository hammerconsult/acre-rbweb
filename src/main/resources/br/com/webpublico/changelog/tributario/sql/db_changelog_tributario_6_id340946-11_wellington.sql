begin
    for dados in (select pvd.id
                         from parcelavalordivida pvd
                        inner join valordivida vd on vd.id = pvd.valordivida_id
                        inner join CALCULODIVIDADIVERSA cdd on cdd.id = vd.CALCULO_ID)
        loop
            proc_atualiza_situacao_valordivida(dados.id);
    end loop;
end;
