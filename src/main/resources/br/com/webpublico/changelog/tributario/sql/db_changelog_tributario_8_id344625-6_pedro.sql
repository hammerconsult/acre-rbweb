insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > TIPO DE INFRACAO DO SAUD > LISTA',
       '/tributario/saud/tipoinfracaosaud/lista.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/tipoinfracaosaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > TIPO DE INFRACAO DO SAUD > EDITA',
       '/tributario/saud/tipoinfracaosaud/edita.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/tipoinfracaosaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > TIPO DE INFRACAO DO SAUD > VISUALIZAR',
       '/tributario/saud/tipoinfracaosaud/visualizar.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/tipoinfracaosaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/lista.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/edita.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/visualizar.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/tipoinfracaosaud/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'TIPO DE INFRACAO DO SAUD', '/tributario/saud/tipoinfracaosaud/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'SAUD'),
       60 from dual
where not exists (select 1 from menu where label = 'TIPO DE INFRACAO DO SAUD');
