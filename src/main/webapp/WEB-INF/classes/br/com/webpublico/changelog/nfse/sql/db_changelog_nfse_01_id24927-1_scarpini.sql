
INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'Notas Fiscais',
                         '/tributario/nfse/nfse/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 10);


INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > NOTAS FISCAIS > LISTA',
     '/tributario/nfse/nfse/lista.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > NOTAS FISCAIS  > VISUALIZA',
     '/tributario/nfse/nfse/visualizar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);
