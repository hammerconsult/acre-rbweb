UPDATE MENU
SET CAMINHO = null
where LABEL = 'MALA DIRETA DO IPTU';

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'MALA DIRETA DO I.P.T.U.', '/tributario/iptu/maladireta/lista.xhtml',
        (select id from menu where LABEL = 'MALA DIRETA DO IPTU'), 10, null);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'PARÂMETRO DA MALA DIRETA DO I.P.T.U.',
        '/tributario/iptu/maladireta/parametromaladireta/lista.xhtml',
        (select id from menu where LABEL = 'MALA DIRETA DO IPTU'), 20, null);


insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > CÁLCULO DE IPTU > PARAMETRO DA MALA DIRETA DO IPTU > LISTA',
       '/tributario/iptu/maladireta/parametromaladireta/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/maladireta/parametromaladireta/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > CÁLCULO DE IPTU > PARAMETRO DA MALA DIRETA DO IPTU > EDITA',
       '/tributario/iptu/maladireta/parametromaladireta/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/maladireta/parametromaladireta/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTARIO > CADASTRO IMOBILIÁRIO > CÁLCULO DE IPTU > PARAMETRO DA MALA DIRETA DO IPTU > VISUALIZAR',
       '/tributario/iptu/maladireta/parametromaladireta/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/maladireta/parametromaladireta/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/iptu/maladireta/parametromaladireta/lista.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/iptu/maladireta/parametromaladireta/edita.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/iptu/maladireta/parametromaladireta/visualizar.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);
