update recursosistema set nome = 'TRIBUTÁRIO > SAUD > PARÂMETRO SAUD > LISTA',
                          caminho = '/tributario/saud/parametrosaud/lista.xhtml'
where caminho = '/tributario/saud/parametrosaud/configura.xhtml';

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > PARÂMETRO SAUD > EDITA',
       '/tributario/saud/parametrosaud/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/parametrosaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > SAUD > PARÂMETRO SAUD > VISUALIZAR',
       '/tributario/saud/parametrosaud/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/saud/parametrosaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/parametrosaud/edita.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/saud/parametrosaud/visualizar.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

update menu set caminho = '/tributario/saud/parametrosaud/lista.xhtml'
where caminho = '/tributario/saud/parametrosaud/configura.xhtml';
