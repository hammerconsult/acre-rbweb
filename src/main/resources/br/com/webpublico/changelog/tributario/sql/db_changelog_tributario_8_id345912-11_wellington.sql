insert into grupoatributo (id, codigo, descricao, ativo, padrao)
values (hibernate_sequence.nextval, 1, 'Geral', 1, 0);

update atributo
set grupoatributo_id = (select id from grupoatributo where codigo = 1)
where grupoatributo_id is null;
