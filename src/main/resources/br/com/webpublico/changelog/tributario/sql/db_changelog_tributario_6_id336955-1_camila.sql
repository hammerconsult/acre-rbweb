
INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > EDITA',
        '/tributario/dividaativa/processodeprotesto/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > VISUALIZA',
        '/tributario/dividaativa/processodeprotesto/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > LISTA',
        '/tributario/dividaativa/processodeprotesto/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval, 'PROCESSO DE PROTESTO',
        '/tributario/dividaativa/processodeprotesto/lista.xhtml',
        (select ID from menu where LABEL = 'DÍVIDA ATIVA'),
        (select max(ORDEM) + 5 from menu where LABEL = 'DÍVIDA ATIVA'), null);
