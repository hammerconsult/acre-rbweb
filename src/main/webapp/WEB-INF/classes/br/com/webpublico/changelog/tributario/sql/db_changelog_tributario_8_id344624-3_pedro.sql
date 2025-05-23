insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > GERENCIAMENTO DA LEGISLAÇÃO > LISTA',
       '/tributario/licenciamentoambiental/legislacao/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/legislacao/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > GERENCIAMENTO DA LEGISLAÇÃO > EDITA',
       '/tributario/licenciamentoambiental/legislacao/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/legislacao/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > GERENCIAMENTO DA LEGISLAÇÃO > VISUALIZAR',
       '/tributario/licenciamentoambiental/legislacao/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/legislacao/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/lista.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/edita.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/visualizar.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/legislacao/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'GERENCIAMENTO DA LEGISLAÇÃO', '/tributario/licenciamentoambiental/legislacao/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'),
       40 from dual
where not exists (select 1 from menu where label = 'GERENCIAMENTO DA LEGISLAÇÃO');
