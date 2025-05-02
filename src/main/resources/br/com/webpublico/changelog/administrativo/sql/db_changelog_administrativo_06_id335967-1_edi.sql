INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > CONVERSÃO DE UNIDADE MEDIDA > EDITA',
        '/administrativo/materiais/conversao-unid-med-mat/edita.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > CONVERSÃO DE UNIDADE MEDIDA > VISUALIZA',
        '/administrativo/materiais/conversao-unid-med-mat/visualizar.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'MATERIAIS > MOVIMENTAÇÕES > CONVERSÃO DE UNIDADE MEDIDA > LISTA',
        '/administrativo/materiais/conversao-unid-med-mat/lista.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval, 'CONVERSÃO DE UNIDADE MEDIDA',
        '/administrativo/materiais/conversao-unid-med-mat/lista.xhtml',
        (select ID from menu where LABEL = 'MOVIMENTAÇÕES'),
        (select max(ORDEM) + 5 from menu where LABEL = 'MOVIMENTAÇÕES'), null);
