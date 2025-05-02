begin
for duplicados in (select setor_id, to_number(codigo) as codigo, max(id) as id_principal
                       from quadra
                       group by setor_id, to_number(codigo)
                       having count(1) > 1)
    loop
        for item in (select q.id
                     from quadra q
                     where q.setor_id = duplicados.setor_id
                       and to_number(q.codigo) = duplicados.codigo
                       and q.id != duplicados.id_principal)
            loop
                for tabelas in (select
                                    a.table_name as referencing_table,
                                    b.column_name as referencing_column
                                from user_constraints a
                                         join user_cons_columns b on a.constraint_name = b.constraint_name
                                         join user_constraints c on a.r_constraint_name = c.constraint_name
                                         join user_cons_columns d on c.constraint_name = d.constraint_name
                                where lower(a.constraint_type) = 'r'
                                  and lower(c.table_name) = lower('quadra')
                                order by a.table_name, a.constraint_name)
                loop
                    execute immediate 'update '||tabelas.referencing_table||' set '||
                        tabelas.referencing_column||' = '||duplicados.id_principal||
                        ' where '||tabelas.referencing_column||' = '||item.id;
end loop;
delete from quadra where id = item.id;
end loop;
end loop;
end;
