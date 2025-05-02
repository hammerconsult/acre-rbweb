INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval, 'COMUM > TEMPLATE DE E-MAIL > LISTAR',
       '/comum/templateemail/lista.xhtml', 0, 'CONFIGURACAO' from dual
where not exists (select 1 from RECURSOSISTEMA where CAMINHO = '/comum/templateemail/lista.xhtml');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval, 'COMUM > TEMPLATE DE E-MAIL > EDITAR',
       '/comum/templateemail/edita.xhtml', 0, 'CONFIGURACAO' from dual
where not exists (select 1 from RECURSOSISTEMA where CAMINHO = '/comum/templateemail/edita.xhtml');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
select hibernate_sequence.nextval, 'COMUM > TEMPLATE DE E-MAIL > VISUALIZAR',
       '/comum/templateemail/visualizar.xhtml', 0, 'CONFIGURACAO' from dual
where not exists (select 1 from RECURSOSISTEMA where CAMINHO = '/comum/templateemail/visualizar.xhtml');

