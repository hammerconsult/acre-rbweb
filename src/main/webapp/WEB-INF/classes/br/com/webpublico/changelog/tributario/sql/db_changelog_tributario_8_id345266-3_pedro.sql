insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'RBTRANS > SAUD > AGENDAMENTO DE LAUDO MEDICO DO SAUD > LISTA',
       '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml',
       0,
       'RBTRANS'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'RBTRANS > SAUD > AGENDAMENTO DE LAUDO MEDICO DO SAUD > EDITA',
       '/tributario/saud/agendamentolaudomedicosaud/edita.xhtml',
       0,
       'RBTRANS'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/saud/agendamentolaudomedicosaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'RBTRANS > SAUD > AGENDAMENTO DE LAUDO MEDICO DO SAUD > VISUALIZAR',
       '/tributario/saud/agendamentolaudomedicosaud/visualizar.xhtml',
       0,
       'RBTRANS'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/saud/agendamentolaudomedicosaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/edita.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/visualizar.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/saud/agendamentolaudomedicosaud/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval,
       'AGENDAMENTO DE LAUDO MÉDICO DO SAUD',
       '/tributario/saud/agendamentolaudomedicosaud/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'SAUD'),
       140
from dual
where not exists (select 1 from menu where label = 'AGENDAMENTO DE LAUDO MÉDICO DO SAUD');
