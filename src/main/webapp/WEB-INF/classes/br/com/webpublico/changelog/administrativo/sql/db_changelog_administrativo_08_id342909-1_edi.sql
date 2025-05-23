INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'LICITACAO > COMPRAS E LICITAÇÕES > CREDENCIAMENTO > EDITAR',
        '/administrativo/licitacao/credenciamento/edita.xhtml', 0, 'LICITACAO');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'LICITACAO > COMPRAS E LICITAÇÕES > CREDENCIAMENTO > VISUALIZAR',
        '/administrativo/licitacao/credenciamento/visualizar.xhtml', 0, 'LICITACAO');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'LICITACAO > COMPRAS E LICITAÇÕES > CREDENCIAMENTO > LISTAR',
        '/administrativo/licitacao/credenciamento/lista.xhtml', 0, 'LICITACAO');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);


INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval, 'CREDENCIAMENTO',
        '/administrativo/licitacao/credenciamento/lista.xhtml',
        (select ID from menu where LABEL = 'COMPRAS E LICITAÇÕES'),
        (select max(ORDEM) + 5 from menu where LABEL = 'LICITAÇÃO'), null);
