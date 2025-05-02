
INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'AIDF-E',
                         '/tributario/nfse/aidfe/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 9);


INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > AIDFE > LISTA',
     '/tributario/nfse/aidfe/lista.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > AIDFE > VISUALIZA',
     '/tributario/nfse/aidfe/visualizar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);
