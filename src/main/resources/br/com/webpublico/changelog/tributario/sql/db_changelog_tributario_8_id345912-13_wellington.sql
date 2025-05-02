update eventoconfiguradobci
set grupoatributo_id = (select id from grupoatributo where codigo = 1)
where grupoatributo_id is null;
