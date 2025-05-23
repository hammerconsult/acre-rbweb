create
or replace procedure proc_faleconosco_reclamante
as
    id_dadospessoais numeric;
begin
for registro in (select * from faleconosconfse where dadosreclamante_id is null)
    loop
select HIBERNATE_SEQUENCE.nextval
into id_dadospessoais
from dual;

insert into dadospessoaisnfse (id, CPFCNPJ, NOMERAZAOSOCIAL, EMAIL, TELEFONE)
values (id_dadospessoais, registro.CPFCNPJ, registro.NOME, registro.EMAIL, registro.TELEFONE);

update FALECONOSCONFSE
set dadosreclamante_id = id_dadospessoais
where id = registro.ID;
end loop;
end;
