INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'ANALISTA RESPONSÁVEL',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/lista.xhtml',
        (select id from menu where LABEL = 'ALVARÁ IMEDIATO'), 70, null);


insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ANALISTA RESPONSÁVEL > LISTA',
       '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho =
                        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ANALISTA RESPONSÁVEL > EDITA',
       '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho =
                        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ANALISTA RESPONSÁVEL > VISUALIZAR',
       '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho =
                        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where
    rs.caminho = '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);
