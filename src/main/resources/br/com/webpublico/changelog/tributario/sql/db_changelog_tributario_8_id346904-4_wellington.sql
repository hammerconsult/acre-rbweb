insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'COMUM > RECURSO EXTERNO > LISTA',
       '/comum/recurso-externo/lista.xhtml', 1, 'CADASTROS' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/comum/recurso-externo/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'COMUM > RECURSO EXTERNO > EDITA',
       '/comum/recurso-externo/edita.xhtml', 1, 'CADASTROS' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/comum/recurso-externo/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'COMUM > RECURSO EXTERNO > VISUALIZA',
       '/comum/recurso-externo/visualizar.xhtml', 1, 'CADASTROS' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/comum/recurso-externo/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'Cadastros Gerais'
  and recursosistema.caminho like '/comum/recurso-externo/%'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'RECURSO EXTERNO',
       '/comum/recurso-externo/lista.xhtml',
       (select id from menu where label = 'CADASTROS GERAIS'),
       (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'CADASTROS GERAIS'))
  from dual
where not exists (select 1 from menu where label = 'RECURSO EXTERNO');
