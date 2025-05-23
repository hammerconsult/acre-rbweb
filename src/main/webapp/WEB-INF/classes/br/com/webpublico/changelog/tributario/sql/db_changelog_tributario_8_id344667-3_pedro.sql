insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > UNIDADE DE MEDIDA > LISTA',
       '/tributario/licenciamentoambiental/unidademedida/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/unidademedida/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > UNIDADE DE MEDIDA > EDITA',
       '/tributario/licenciamentoambiental/unidademedida/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/unidademedida/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTARIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > UNIDADE DE MEDIDA > VISUALIZAR',
       '/tributario/licenciamentoambiental/unidademedida/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/unidademedida/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/lista.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/edita.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/visualizar.xhtml'
  and gr.nome = 'TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/unidademedida/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'UNIDADE DE MEDIDA AMBIENTAL', '/tributario/licenciamentoambiental/unidademedida/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'),
       80 from dual
where not exists (select 1 from menu where label = 'UNIDADE DE MEDIDA AMBIENTAL');
