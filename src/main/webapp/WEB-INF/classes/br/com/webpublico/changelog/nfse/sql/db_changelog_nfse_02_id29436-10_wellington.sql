INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > SIMPLES NACIONAL > EVENTOS SIMPLES NACIONAL > LISTA',
     '/tributario/simples-nacional/evento-simples-nacional/importar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'EVENTOS SIMPLES NACIONAL',
                         '/tributario/simples-nacional/evento-simples-nacional/importar.xhtml', (select ID from menu where LABEL = 'SIMPLES NACIONAL'), 30);
