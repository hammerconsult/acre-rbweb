insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCURADORIA > SOLICITAÇÃO DE PARCELAMENTO JUDICIAL > LISTA',
       '/tributario/dividaativa/parcelamentojudicial/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/dividaativa/parcelamentojudicial/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCURADORIA > SOLICITAÇÃO DE PARCELAMENTO JUDICIAL > EDITA',
       '/tributario/dividaativa/parcelamentojudicial/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/dividaativa/parcelamentojudicial/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCURADORIA > SOLICITAÇÃO DE PARCELAMENTO JUDICIAL > VISUALIZAR',
       '/tributario/dividaativa/parcelamentojudicial/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/dividaativa/parcelamentojudicial/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho like '/tributario/dividaativa/parcelamentojudicial/%'
  and gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'SOLICITAÇÃO DE PARCELAMENTO JUDICIAL',
       '/tributario/dividaativa/parcelamentojudicial/lista.xhtml', 451698327, 40
from dual
where not exists (select 1 from menu where caminho = '/tributario/dividaativa/parcelamentojudicial/lista.xhtml');
