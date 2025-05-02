DELETE FROM MENU WHERE CAMINHO = '/tributario/nfse/nfse/lista.xhtml';
INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'NOTAS FISCAIS',
                         '/tributario/nfse/nfse/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 10);
