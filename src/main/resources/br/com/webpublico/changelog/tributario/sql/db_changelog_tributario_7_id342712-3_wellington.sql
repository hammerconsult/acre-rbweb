create
or replace function get_next_numero_dam(p_exercicio numeric)
    return numeric
as
    PRAGMA AUTONOMOUS_TRANSACTION;
    v_sequence_name
varchar(100);
    v_numero
numeric;
    v_exists_sequence
int;
    v_ddl
varchar(250);
begin
    v_sequence_name
:= 'dam_sequence_' || p_exercicio;
select count(1)
into v_exists_sequence
from user_sequences s
where lower(s.sequence_name) = lower(v_sequence_name);
if
v_exists_sequence = 0 then
select coalesce(max(d.numero), 0) + 1
into v_numero
from dam d
         inner join exercicio e on e.id = d.exercicio_id
where e.ano = p_exercicio;
v_ddl
:= '
create sequence ' || v_sequence_name || ' start with ' || v_numero || ' increment by 1 ';
execute immediate v_ddl;
end if;
execute immediate 'select ' || v_sequence_name || '.nextval from dual ' into v_numero;
return v_numero;
end;
