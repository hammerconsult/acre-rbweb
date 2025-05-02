declare
    idx int;
begin
    for duplicados in (select exercicio_id, numero, count(1)
                       from processoparcelamento
                       group by exercicio_id, numero
                       having count(1) > 1)
        loop
            idx := 0;
            for dados in (select id from processoparcelamento pp
                          where pp.exercicio_id = duplicados.exercicio_id
                            and pp.numero = duplicados.numero
                          order by pp.id desc)
                loop
                    if (idx > 0) then
                        update processoparcelamento set descartenumerounico = seq_descarte_numero_unico.nextval
                        where id = dados.id;
                    end if;
                    idx := idx + 1;
                end loop;
        end loop;
end;

