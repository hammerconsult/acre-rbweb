insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO IMOBILIÁRIO > ENDEREÇAMENTO > ZONA FISCAL > LISTA',
       '/tributario/cadastromunicipal/zonafiscal/lista.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/cadastromunicipal/zonafiscal/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO IMOBILIÁRIO > ENDEREÇAMENTO > ZONA FISCAL > EDITA',
       '/tributario/cadastromunicipal/zonafiscal/edita.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/cadastromunicipal/zonafiscal/edita.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO IMOBILIÁRIO > ENDEREÇAMENTO > ZONA FISCAL > VISUALIZAR',
       '/tributario/cadastromunicipal/zonafiscal/visualizar.xhtml',
       1,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/cadastromunicipal/zonafiscal/visualizar.xhtml');

insert into GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and rs.caminho in ('/tributario/cadastromunicipal/zonafiscal/lista.xhtml',
                     '/tributario/cadastromunicipal/zonafiscal/edita.xhtml',
                     '/tributario/cadastromunicipal/zonafiscal/visualizar.xhtml')
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.GRUPORECURSO_ID = gr.ID
                    and grs.RECURSOSISTEMA_ID = rs.ID);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
select hibernate_sequence.nextval,
       'ZONA FISCAL',
       '/tributario/cadastromunicipal/zonafiscal/lista.xhtml',
       -126757950,
       (SELECT MAX(ORDEM) + 10 FROM MENU WHERE PAI_ID = -126757950),
       null
from dual
where not exists (select 1 from menu where CAMINHO = '/tributario/cadastromunicipal/zonafiscal/lista.xhtml');


