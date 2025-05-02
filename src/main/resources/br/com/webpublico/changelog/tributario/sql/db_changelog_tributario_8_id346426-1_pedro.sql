insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'RBTRANS > PARAMETRO INFORMACOES > LISTA',
       '/tributario/rbtrans/parametros/informacoes/lista.xhtml',
       0,
       'RBTRANS'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/rbtrans/parametros/informacoes/lista.xhtml');

UPDATE RECURSOSISTEMA
SET NOME = 'RBTRANS > PARAMETRO INFORMACOES > EDITA'
WHERE CAMINHO = '/tributario/rbtrans/parametros/informacoes/edita.xhtml';

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'RBTRANS > PARAMETRO INFORMACOES > VISUALIZA',
       '/tributario/rbtrans/parametros/informacoes/visualiza.xhtml',
       0,
       'RBTRANS'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/rbtrans/parametros/informacoes/visualiza.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/rbtrans/parametros/informacoes/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/rbtrans/parametros/informacoes/edita.xhtml"'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/rbtrans/parametros/informacoes/visualiza.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

UPDATE MENU
SET CAMINHO= '/tributario/rbtrans/parametros/informacoes/lista.xhtml'
WHERE LABEL = 'INFORMAÇÕES GERAIS - RBTRANS';
