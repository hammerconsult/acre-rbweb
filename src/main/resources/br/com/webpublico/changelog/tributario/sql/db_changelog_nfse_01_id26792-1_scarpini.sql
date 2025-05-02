INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'EVENTOS REDE SIM',
        '/tributario/cadastromunicipal/cadastroeconomico/eventosredesim.xhtml',
        (select ID from menu where LABEL = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.'),
        20);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C. > EVENTOS REDE SIM',
        '/tributario/cadastromunicipal/cadastroeconomico/eventosredesim.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

