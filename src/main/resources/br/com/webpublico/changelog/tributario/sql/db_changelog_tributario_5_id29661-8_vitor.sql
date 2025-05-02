INSERT INTO RECURSOSISTEMA VALUES
(HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > SIMPLES NACIONAL > CADASTRO DE OFÍCIOS MEI > IMPORTAR',
 '/tributario/simples-nacional/cadastro-oficios-MEI/importar.xhtml', 0, 'TRIBUTARIO');


INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO MENU VALUES (HIBERNATE_SEQUENCE.nextval, 'IMPORTAÇÃO DE OFÍCIOS MEI',
                         '/tributario/simples-nacional/cadastro-oficios-MEI/importar.xhtml', (select ID from MENU where LABEL = 'SIMPLES NACIONAL'), 31);

