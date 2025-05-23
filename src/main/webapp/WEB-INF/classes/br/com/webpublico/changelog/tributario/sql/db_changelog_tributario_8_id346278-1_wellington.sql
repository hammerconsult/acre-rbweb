create
or replace procedure proc_insere_unidade_medida_ambiental(p_descricao in varchar, p_tipo in varchar)
is
begin
insert into unidademedidaambiental (id, tipounidade, descricao, ativo)
select hibernate_sequence.nextval, p_tipo, p_descricao, 1
from dual
where not exists (select 1
                  from unidademedidaambiental
                  where descricao = p_descricao);
end;
