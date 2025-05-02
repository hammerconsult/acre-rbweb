insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > API KEY > LISTA',
        '/comum/api-key/lista.xhtml', 0, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > API KEY > EDITA',
        '/comum/api-key/edita.xhtml', 1, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > API KEY > VISUALIZAR',
        '/comum/api-key/visualizar.xhtml', 0, 'CADASTROS');
insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'API KEY',
       '/comum/api-key/lista.xhtml',
       (select id from menu where label = 'CADASTROS GERAIS'),
       0
from dual
where not exists (select 1 from menu where label = 'API KEY');
insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/comum/api-key/lista.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/comum/api-key/edita.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/comum/api-key/visualizar.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
