insert into dividalicenciamentoambiental (id, divida_id)
select HIBERNATE_SEQUENCE.nextval, param.divida_id
from parametrolicenciamentoambiental param;

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'DÍVIDA DO LICENCIAMENTO AMBIENTAL',
        '/tributario/licenciamentoambiental/divida/lista.xhtml',
        (select ID from menu where LABEL = 'LICENCIAMENTO AMBIENTAL'), 35, null);

update menu
set LABEL   = 'DOCUMENTAÇÃO DO LICENCIAMENTO AMBIENTAL',
    CAMINHO = '/tributario/licenciamentoambiental/divida/lista.xhtml'
WHERE LABEL = 'PARÂMETRO'
  AND PAI_ID = (select ID from menu where LABEL = 'LICENCIAMENTO AMBIENTAL');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'LICENCIAMENTO AMBIENTAL > DIVIDA DO LICENCIAMENTO > LISTA',
       '/tributario/licenciamentoambiental/divida/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/divida/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'LICENCIAMENTO AMBIENTAL > DIVIDA DO LICENCIAMENTO > EDITA',
       '/tributario/licenciamentoambiental/divida/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/licenciamentoambiental/divida/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'LICENCIAMENTO AMBIENTAL > DIVIDA DO LICENCIAMENTO > VISUALIZAR',
       '/tributario/licenciamentoambiental/divida/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/licenciamentoambiental/divida/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/divida/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/divida/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/divida/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);
