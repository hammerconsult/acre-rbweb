INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > SOLICITAÇÃO > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/lista.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > SOLICITAÇÃO > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/edita.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > SOLICITAÇÃO > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/visualiza.xhtml', 1,
        'TRIBUTARIO');

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'SOLICITAÇÃO DE ALVARÁ IMEDIATO',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/solicitacao/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        50);
