declare
    num_registros number;
begin
    select count(*) into num_registros from condominio where codigo is null;
    for i in 1..num_registros
        loop
            update condominio
            set codigo = i
            where rownum = 1
              and codigo is null;
        end loop;
end;
