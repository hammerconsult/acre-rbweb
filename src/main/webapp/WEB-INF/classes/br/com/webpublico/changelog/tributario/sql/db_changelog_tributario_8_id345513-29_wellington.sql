
declare
v_seq integer;
begin
for cadastro in (select id from cadastroeconomico)
    loop
        v_seq := 1;
for registro in (select id from enderecocadastroeconomico
                         where tipoendereco = 'CORRESPONDENCIA'
                           and cadastroeconomico_id = cadastro.id
                         order by id)
        loop
update enderecocadastroeconomico set SEQUENCIAL = v_seq where id = registro.ID;
v_seq := v_seq + 1;
end loop;
end loop;
end;
