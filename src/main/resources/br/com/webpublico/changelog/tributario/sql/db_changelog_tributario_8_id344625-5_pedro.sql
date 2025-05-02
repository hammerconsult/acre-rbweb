insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > INFRACAO DO SAUD > LISTA',
       '/tributario/saud/infracaosaud/lista.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/infracaosaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > INFRACAO DO SAUD > EDITA',
       '/tributario/saud/infracaosaud/edita.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/infracaosaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > INFRACAO DO SAUD > VISUALIZAR',
       '/tributario/saud/infracaosaud/visualizar.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/infracaosaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/lista.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/edita.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/visualizar.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/infracaosaud/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'INFRACAO DO SAUD', '/tributario/saud/infracaosaud/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'SAUD'),
       60 from dual
where not exists (select 1 from menu where label = 'INFRACAO DO SAUD');
