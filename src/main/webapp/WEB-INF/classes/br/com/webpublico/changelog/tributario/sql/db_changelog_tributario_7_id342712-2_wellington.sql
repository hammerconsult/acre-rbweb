create
    or replace trigger trigger_gerar_numerodam
    before insert
    on dam
    for each row
declare
    v_numero numeric;
    v_ano    int;
begin
    select ano
    into v_ano
    from exercicio
    where id = :new.exercicio_id;
    select get_next_numero_dam(v_ano)
    into v_numero
    from dual;
    :new.numero := v_numero;
    :new.numerodam := v_numero || '/' || v_ano;
end;
