insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > LISTA',
       '/tributario/parametromarcafogo/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/parametromarcafogo/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > EDITA',
       '/tributario/parametromarcafogo/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/parametromarcafogo/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > VISUALIZAR',
       '/tributario/parametromarcafogo/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/parametromarcafogo/visualizar.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > LISTA',
       '/tributario/marcafogo/lista.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/marcafogo/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > EDITA',
       '/tributario/marcafogo/edita.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/marcafogo/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > VISUALIZAR',
       '/tributario/marcafogo/visualizar.xhtml', 1, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema
                  where caminho = '/tributario/marcafogo/visualizar.xhtml');

insert into GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
select gr.id, rs.id from gruporecurso gr, recursosistema rs
where gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and rs.nome in ('TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > LISTA',
                  'TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > EDITA',
                  'TRIBUTÁRIO > PARÂMETRO DE MARCA A FOGO > VISUALIZAR',
                  'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > LISTA',
                  'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > EDITA',
                  'TRIBUTÁRIO > LANÇAMENTO DE MARCA A FOGO > VISUALIZAR')
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.GRUPORECURSO_ID = gr.id
                    and grs.RECURSOSISTEMA_ID = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select HIBERNATE_SEQUENCE.nextval, 'MARCA A FOGO', null,
       (select id from menu where label = 'ATENDIMENTO AO CONTRIBUINTE'),
       100 from dual
where not exists (select 1 from menu where label = 'MARCA A FOGO');

insert into menu (id, label, caminho, pai_id, ordem)
select HIBERNATE_SEQUENCE.nextval, 'PARÂMETRO DE MARCA A FOGO', '/tributario/parametromarcafogo/lista.xhtml',
       (select id from menu where label = 'MARCA A FOGO'),
       1 from dual
where not exists (select 1 from menu where label = 'PARÂMETRO DE MARCA A FOGO');

insert into menu (id, label, caminho, pai_id, ordem)
select HIBERNATE_SEQUENCE.nextval, 'LANÇAMENTO DE MARCA A FOGO', '/tributario/marcafogo/lista.xhtml',
       (select id from menu where label = 'MARCA A FOGO'),
       50 from dual
where not exists (select 1 from menu where label = 'LANÇAMENTO DE MARCA A FOGO');
