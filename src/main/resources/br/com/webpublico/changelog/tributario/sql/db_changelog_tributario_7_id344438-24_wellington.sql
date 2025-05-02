insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > PARÂMETRO SAUD > CONFIGURA',
       '/tributario/saud/parametrosaud/configura.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/parametrosaud/configura.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > USUÁRIO SAUD > LISTA',
       '/tributario/saud/usuariosaud/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/usuariosaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > USUÁRIO SAUD > EDITA',
       '/tributario/saud/usuariosaud/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/usuariosaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > USUÁRIO SAUD > VISUALIZAR',
       '/tributario/saud/usuariosaud/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/usuariosaud/visualizar.xhtml');


insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/parametrosaud/configura.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/usuariosaud/lista.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/usuariosaud/edita.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/usuariosaud/visualizar.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, pai_id, ordem)
select hibernate_sequence.nextval,
       'SAUD',
       (select id from menu where label = 'TRIBUTÁRIO'),
       (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'TRIBUTÁRIO'))
  from dual
where not exists (select 1 from menu where label = 'SAUD');

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'PARÂMETRO DO SAUD',
       '/tributario/saud/parametrosaud/configura.xhtml',
       (select id from menu where label = 'SAUD'),
       0
  from dual
where not exists (select 1 from menu where label = 'PARÂMETRO DO SAUD');

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'USUÁRIO DO SAUD',
       '/tributario/saud/usuariosaud/lista.xhtml',
       (select id from menu where label = 'SAUD'),
       20
from dual
where not exists (select 1 from menu where label = 'USUÁRIO DO SAUD');
