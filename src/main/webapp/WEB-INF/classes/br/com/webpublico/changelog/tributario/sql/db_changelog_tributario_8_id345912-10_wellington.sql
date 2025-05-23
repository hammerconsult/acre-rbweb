insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > GRUPO DE ATRIBUTO GENÉRICO > LISTA',
       '/tributario/cadastromunicipal/grupoatributo/lista.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/cadastromunicipal/grupoatributo/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > GRUPO DE ATRIBUTO GENÉRICO > EDITA',
       '/tributario/cadastromunicipal/grupoatributo/edita.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/cadastromunicipal/grupoatributo/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > GRUPO DE ATRIBUTO GENÉRICO > VISUALIZAR',
       '/tributario/cadastromunicipal/grupoatributo/visualizar.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/cadastromunicipal/grupoatributo/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho in ('/tributario/cadastromunicipal/grupoatributo/lista.xhtml',
                     '/tributario/cadastromunicipal/grupoatributo/edita.xhtml',
                     '/tributario/cadastromunicipal/grupoatributo/visualizar.xhtml')
  and gr.nome in ('ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'GRUPOS DE ATRIBUTOS GENÉRICOS',
       '/tributario/cadastromunicipal/grupoatributo/lista.xhtml',
       (select id from menu where label = 'TABELAS E CONFIGURAÇÕES GERAIS'),
       110
from dual
where not exists (select 1 from menu where label = 'GRUPOS DE ATRIBUTOS GENÉRICOS');
