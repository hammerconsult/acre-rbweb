INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'ALVARÁ IMEDIATO', null,
        (select id from menu where label = 'CADASTRO TÉCNICO IMOBILIÁRIO'),
        100);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'ZONA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        5);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'CLASSIFICAÇÃO DE USO',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        10);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'HIERARQUIA DA VIA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        10);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'PERMISSÃO DE USO DO ZONEAMENTO',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/lista.xhtml',
        (select id from menu where label = 'ALVARÁ IMEDIATO'),
        50);

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ZONA > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/lista.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ZONA > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/edita.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > ZONA > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/zona/visualiza.xhtml', 1, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > CLASSIFICAÇÃO DE USO > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/lista.xhtml', 1,
        'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > CLASSIFICAÇÃO DE USO > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/edita.xhtml', 1,
        'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > CLASSIFICAÇÃO DE USO > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/visualiza.xhtml', 1,
        'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > HIERARQUIA DA VIA > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/lista.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > HIERARQUIA DA VIA > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/edita.xhtml', 1, 'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > HIERARQUIA DA VIA > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/hierarquiavia/visualiza.xhtml', 1,
        'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PERMISSÃO DE USO DO ZONEAMENTO > LISTA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/lista.xhtml', 1,
        'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PERMISSÃO DE USO DO ZONEAMENTO > EDITA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/edita.xhtml', 1,
        'TRIBUTARIO');
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > ALVARÁ IMEDIATO > PERMISSÃO DE USO DO ZONEAMENTO > VISUALIZA',
        '/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/permissaousozoneamento/visualiza.xhtml', 1,
        'TRIBUTARIO');
