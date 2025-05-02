insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > ALVARA > PROCESSO SUSPENSA CASSACAO ALVARA > LISTA',
       '/tributario/alvara/suspensao/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/alvara/suspensao/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > ALVARA > PROCESSO SUSPENSA CASSACAO ALVARA > EDITA',
       '/tributario/alvara/suspensao/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/alvara/suspensao/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > ALVARA > PROCESSO SUSPENSA CASSACAO ALVARA > VISUALIZAR',
       '/tributario/alvara/suspensao/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/alvara/suspensao/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/alvara/suspensao/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/alvara/suspensao/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/alvara/suspensao/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval,
       'SUSPENSÃO E CASSAÇÃO DE ALVARÁ',
       '/tributario/alvara/suspensao/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'ALVARÁ'),
       80
from dual
where not exists (select 1 from menu where label = 'SUSPENSÃO E CASSAÇÃO DE ALVARÁ');
