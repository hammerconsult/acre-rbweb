insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > MOTORISTA DO SAUD > LISTA',
       '/tributario/saud/motoristasaud/lista.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/motoristasaud/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > MOTORISTA DO SAUD > EDITA',
       '/tributario/saud/motoristasaud/edita.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/motoristasaud/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'RBTRANS > SAUD > MOTORISTA DO SAUD > VISUALIZAR',
       '/tributario/saud/motoristasaud/visualizar.xhtml', 0, 'RBTRANS' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/saud/motoristasaud/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/motoristasaud/lista.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/motoristasaud/edita.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/saud/motoristasaud/visualizar.xhtml'
  and gr.nome = 'RBT - SAUD - RBTRANS'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'MOTORISTA DO SAUD', '/tributario/saud/motoristasaud/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'SAUD'),
       20 from dual
where not exists (select 1 from menu where label = 'MOTORISTA DO SAUD');
