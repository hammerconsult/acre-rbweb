insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > TECNICO > LISTA',
       '/tributario/licenciamentoambiental/tecnico/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/tecnico/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > TECNICO > EDITA',
       '/tributario/licenciamentoambiental/tecnico/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/tecnico/edita.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > TECNICO > VISUALIZA',
       '/tributario/licenciamentoambiental/tecnico/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/tecnico/visualizar.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > CONTROLE > LISTA',
       '/tributario/licenciamentoambiental/controle/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/controle/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > CONTROLE > EDITA',
       '/tributario/licenciamentoambiental/controle/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/controle/edita.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > CONTROLE > VISUALIZA',
       '/tributario/licenciamentoambiental/controle/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/controle/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/tecnico/lista.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/tecnico/edita.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/tecnico/visualizar.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/controle/lista.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/controle/edita.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/controle/visualizar.xhtml'
  and gr.nome in ('TRB - FISCALIZAÇÃO AMBIENTAL - SEMEIA', 'ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

update menu set ordem = 0, label = 'PARÂMETRO DE LICENCIAMENTO AMBIENTAL' where id = 10984586999;
update menu set ordem = 10, label = 'ASSUNTO DE LICENCIAMENTO AMBIENTAL' where id = 10979818666;
update menu set ordem = 20, label = 'LEGISLAÇÃO DE LICENCIAMENTO AMBIENTAL' where id = 10996587291;
update menu set ordem = 30, label = 'UNIDADE DE MEDIDA DE LICENCIAMENTO AMBIENTAL' where id = 10997601733;
update menu set ordem = 100, label = 'PROCESSO DE LICENCIAMENTO AMBIENTAL' where id = 10979818670;

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'TÉCNICO DE LICENCIAMENTO AMBIENTAL', '/tributario/licenciamentoambiental/tecnico/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'),
       40 from dual
where not exists (select 1 from menu where label = 'TÉCNICO DE LICENCIAMENTO AMBIENTAL');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'CONTROLE DE LICENCIAMENTO AMBIENTAL', '/tributario/licenciamentoambiental/controle/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'),
       110 from dual
where not exists (select 1 from menu where label = 'CONTROLE DE LICENCIAMENTO AMBIENTAL');
