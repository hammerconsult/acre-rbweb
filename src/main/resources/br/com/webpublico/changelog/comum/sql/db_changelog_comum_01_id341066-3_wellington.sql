create or replace package body pkg_fks as

    function get_fks(p_table_name varchar2)
        return fk_table pipelined is rec fk_record;
begin
for rec in (select cc.table_name, cc.column_name
                    from user_constraints c
                             inner join user_cons_columns cc on cc.constraint_name = c.constraint_name
                             inner join user_constraints cr on cr.constraint_name = c.r_constraint_name
                             inner join user_cons_columns crc on crc.constraint_name = cr.constraint_name
                    where lower(cr.table_name) = lower(p_table_name)
                      and lower(cr.constraint_type) = 'p')
            loop
                pipe row ( rec );
end loop;
        return;
end get_fks;

    function has_record(p_table varchar2, p_column varchar2, p_value numeric)
        return number
    as
        count_record numeric(19);
begin

execute immediate ' select count(1) from '||p_table||' where '||p_column||' = '||p_value into count_record;

if (count_record > 0) then
            return 1;
end if;

return 0;
end has_record;

    function get_fks_with_movement(p_table_name varchar2, p_id numeric)
    return fk_table pipelined is
begin
for item in (select c.table_name, c.column_name
                       from get_fks(p_table_name) c)
        loop
            if (has_record(item.table_name, item.column_name, p_id) = 1) then
                pipe row ( item );
end if;
end loop;
        return;
end get_fks_with_movement;
end;
