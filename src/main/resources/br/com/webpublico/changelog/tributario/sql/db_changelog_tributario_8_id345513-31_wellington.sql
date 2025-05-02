
declare
v_codigo numeric;
begin
for exercicio in (select distinct pc.exercicio_id as id
                        from processocalculoalvara sp
                       inner join processocalculo pc on pc.id = sp.id
                     order by pc.EXERCICIO_ID)
    loop
        v_codigo := 1;
for processo in (select pca.id
                            from processocalculoalvara pca
                           inner join processocalculo pc on pc.id = pca.id
                         where pc.exercicio_id = exercicio.id
                         order by pca.id)
        loop
update processocalculoalvara set codigo = v_codigo
where id = processo.id;
v_codigo := v_codigo + 1;
end loop;
end loop;
end;
