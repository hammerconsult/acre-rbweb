INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'LIVROS FISCAIS',
                         '/tributario/nfse/livrofiscal/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 20);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > LIVROS FISCAIS > LISTA',
     '/tributario/nfse/livrofiscal/lista.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

