insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > CONTROLE AMBIENTAL > LICENCIAMENTO AMBIENTAL > DOCUMENTAÇÃO DO LICENCIAMENTO AMBIENTAL',
       '/tributario/licenciamentoambiental/documentacao/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/licenciamentoambiental/documentacao/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > CONTROLE AMBIENTAL > LICENCIAMENTO AMBIENTAL > DOCUMENTAÇÃO DO LICENCIAMENTO AMBIENTAL > EDITA',
       '/tributario/licenciamentoambiental/documentacao/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/licenciamentoambiental/documentacao/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > CONTROLE AMBIENTAL > LICENCIAMENTO AMBIENTAL > DOCUMENTAÇÃO DO LICENCIAMENTO AMBIENTAL > VISUALIZAR',
       '/tributario/licenciamentoambiental/documentacao/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/licenciamentoambiental/documentacao/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/documentacao/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/licenciamentoambiental/documentacao/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/licenciamentoambiental/documentacao/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval,
       'PARÂMETRO DO LICENCIAMENTO AMBIENTAL',
       '/tributario/licenciamentoambiental/parametro/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'CONFIGURAÇÕES DE LICENCIAMENTO AMBIENTAL'),
       120
from dual
where not exists (select 1 from menu where label = 'PARÂMETRO DO LICENCIAMENTO AMBIENTAL');
