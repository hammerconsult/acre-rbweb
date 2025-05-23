INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PARÂMETRO > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/lista.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PARÂMETRO > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/edita.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PARÂMETRO > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/visualiza.xhtml', 1, 'TRIBUTARIO');

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'PARÂMETRO DE ALVARÁ IMEDIATO',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        1);
