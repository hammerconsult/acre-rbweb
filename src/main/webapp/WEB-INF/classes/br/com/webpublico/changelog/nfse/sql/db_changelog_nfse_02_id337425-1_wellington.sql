create
or replace trigger definenumeronotafiscal
    before insert
    on notafiscal
    for each row
declare
v_existe_sequence numeric;
    v_prestador_id
numeric;
    v_homologacao
numeric;
    v_sequence_name
varchar(100);
begin
    v_prestador_id
:= :new.prestador_id;
    v_homologacao
:= :new.homologacao;
    if
(v_homologacao is null) then
        v_homologacao := 0;
end if;
    v_sequence_name
:= 'seq_numero_nfse_'||v_prestador_id||'_'||v_homologacao;

select count(1)
into v_existe_sequence
from user_sequences q
where lower(q.sequence_name) = v_sequence_name;

if
(v_existe_sequence = 0) then
        execute immediate 'create sequence '||v_sequence_name;
end if;

execute immediate 'select ' || v_sequence_name || '.nextval from dual' into :new.numero;
end;
