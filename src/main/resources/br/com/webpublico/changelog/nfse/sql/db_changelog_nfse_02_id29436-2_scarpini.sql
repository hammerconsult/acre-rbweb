INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES DA NFSE',
                         '/tributario/nfse/configuracao/configurar.xhtml', (select ID from menu where LABEL = 'NFS-E'), 20);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > CONFIGURAÇÕES',
     '/tributario/nfse/configuracao/configurar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

