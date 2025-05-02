INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > EXCLUSÃO DE MATERIAIS > EDITA',
        '/administrativo/materiais/exclusao-materiais/edita.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > EXCLUSÃO DE MATERIAIS > VISUALIZA',
        '/administrativo/materiais/exclusao-materiais/visualizar.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > EXCLUSÃO DE MATERIAIS > LISTA',
        '/administrativo/materiais/exclusao-materiais/lista.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval, 'EXCLUSÃO DE MATERIAIS',
        '/administrativo/materiais/exclusao-materiais/lista.xhtml',
        (select ID from menu where LABEL = 'MOVIMENTAÇÕES'),
        (select max(ORDEM) + 5 from menu where LABEL = 'MOVIMENTAÇÕES'), null);
