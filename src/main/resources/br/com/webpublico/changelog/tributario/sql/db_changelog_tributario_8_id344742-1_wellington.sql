create or replace trigger trigger_gerar_numero_parcelamento
    before insert
    on processoparcelamento
    for each row
declare
    v_numero numeric;
begin
    select coalesce(max(pp.numero), 0) + 1
    into v_numero
    from processoparcelamento pp
    where pp.exercicio_id = :new.exercicio_id;
    :new.numero := v_numero;
end;
