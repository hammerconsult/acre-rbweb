--insert menu principal
INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'AVALIAÇÕES E QUESTIONÁRIOS',
        '',
        (SELECT ID FROM MENU WHERE LABEL = 'RECURSOS HUMANOS'),
        181);

--insert itens
INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'MONTAGEM DO QUESTIONÁRIO',
        '/rh/avaliacao/montagem-avaliacao/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'AVALIAÇÕES E QUESTIONÁRIOS'),
        1);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'NIVEL RESPOSTA',
        '/rh/avaliacao/nivel-resposta/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'AVALIAÇÕES E QUESTIONÁRIOS'),
        2);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'AVALIADOS',
        '/rh/avaliacao/avaliacao-rh/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'AVALIAÇÕES E QUESTIONÁRIOS'),
        3);
