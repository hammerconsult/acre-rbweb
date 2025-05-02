insert into grupoatributo (id, codigo, descricao, ativo, padrao)
select hibernate_sequence.nextval, 2, 'IPTU ANTIGO', 1, 0
from dual
where not exists (select 1 from grupoatributo where codigo = 2);

insert into grupoatributo (id, codigo, descricao, ativo, padrao)
select hibernate_sequence.nextval, 3, 'NOVO IPTU', 1, 1
from dual
where not exists (select 1 from grupoatributo where codigo = 3);

update atributo
set grupoatributo_id = (select id from grupoatributo where codigo = 2)
where grupoatributo_id = (select id from grupoatributo where codigo = 1)
  and classedoatributo in ('CADASTRO_IMOBILIARIO', 'CONSTRUCAO', 'LOTE');

update eventoconfiguradobci
set grupoatributo_id = (select id from grupoatributo where codigo = 2)
where grupoatributo_id = (select id from grupoatributo where codigo = 1);
