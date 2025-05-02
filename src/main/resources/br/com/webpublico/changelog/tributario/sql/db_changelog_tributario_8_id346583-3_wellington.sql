insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > COMUNICAÇÃO SOFTPLAN > LISTA',
       '/tributario/comunicacaosoftplan/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/comunicacaosoftplan/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > COMUNICAÇÃO SOFTPLAN > VISUALIZAR',
       '/tributario/comunicacaosoftplan/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/comunicacaosoftplan/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho like '/tributario/comunicacaosoftplan/%'
  and gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval,
       'COMUNICAÇÃO SOFTPLAN',
       '/tributario/comunicacaosoftplan/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'DÍVIDA ATIVA'),
       160
from dual
where not exists (select 1 from menu where caminho = '/tributario/comunicacaosoftplan/lista.xhtml');
