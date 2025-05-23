insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > AGENDAMENTO DE VIAGEM DO SAUD > LISTA',
       '/tributario/saud/agendamentoviagemsaud/lista.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/agendamentoviagemsaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > AGENDAMENTO DE VIAGEM DO SAUD > EDITA',
       '/tributario/saud/agendamentoviagemsaud/edita.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/agendamentoviagemsaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > AGENDAMENTO DE VIAGEM DO SAUD > VISUALIZAR',
       '/tributario/saud/agendamentoviagemsaud/visualizar.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/agendamentoviagemsaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/lista.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/edita.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/visualizar.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/agendamentoviagemsaud/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'AGENDAMENTO DE VIAGEM DO SAUD', '/tributario/saud/agendamentoviagemsaud/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'SAUD'),
       100 from dual
where not exists (select 1 from menu where label = 'AGENDAMENTO DE VIAGEM DO SAUD');
