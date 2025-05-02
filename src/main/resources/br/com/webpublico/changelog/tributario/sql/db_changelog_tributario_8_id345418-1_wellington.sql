begin
for registro in (select sp.id, sp.exercicio_id
                        from processocalculoalvara sp
                     order by sp.id)
    loop
update processocalculoalvara
set codigo = (select coalesce(max(codigo), 0) + 1
              from processocalculoalvara sp
              where sp.exercicio_id = registro.EXERCICIO_ID)
where id = registro.id;
end loop;
end;
