declare
proximo_sequencial numeric;
    id_tecnico
numeric;
begin
    proximo_sequencial
:= 1;
for registro in (select id from TECNICOLICENCIAMENTOAMBIENTAL order by id)
    loop
update TECNICOLICENCIAMENTOAMBIENTAL
set sequencial = proximo_sequencial
where id = registro.id;
proximo_sequencial
:= proximo_sequencial + 1;
end loop;

    proximo_sequencial
:= 1;
for registro in (select id from PROCESSOLICENCIAMENTOAMBIENTAL order by id)
    loop
update PROCESSOLICENCIAMENTOAMBIENTAL
set sequencial = proximo_sequencial
where id = registro.id;
proximo_sequencial
:= proximo_sequencial + 1;
end loop;

    proximo_sequencial
:= 1;
for registro in (select id from PROCESSOLICENCIAMENTOAMBIENTAL order by id)
        loop
select MIN(id)
into id_tecnico
from tecnicolicenciamentoambiental
where ATIVO = 1
  AND sequencial >= proximo_sequencial;
if
(id_tecnico is null) then
                proximo_sequencial := 1;
select MIN(id)
into id_tecnico
from tecnicolicenciamentoambiental
where ATIVO = 1
  AND sequencial >= proximo_sequencial;
end if;
            if
(id_tecnico is not null) then
                insert into tecnicoprocessolicenciamentoambiental (id, dataregistro, processolicenciamentoambiental_id,
                                                                   tecnicolicenciamentoambiental_id, principal)
                values (HIBERNATE_SEQUENCE.nextval, current_date, registro.id, id_tecnico, 1);
end if;
select sequencial + 1
into proximo_sequencial
from TECNICOLICENCIAMENTOAMBIENTAL
where id = id_tecnico;
end loop;
end;
