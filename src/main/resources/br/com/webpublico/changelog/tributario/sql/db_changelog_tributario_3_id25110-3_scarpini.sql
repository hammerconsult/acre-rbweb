insert into revisaoauditoria(id, datahora, ip, usuario)
select 1, '01/01/1980', 'localhost', 'WebPúblico (Migração de Dados)' from dual where not exists (select id from revisaoauditoria where id = 1);
