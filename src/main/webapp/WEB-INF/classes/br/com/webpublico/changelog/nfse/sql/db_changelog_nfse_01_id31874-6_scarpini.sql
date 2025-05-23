INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'LOTE DE DECLARACOES MENSAIS',
                         '/tributario/nfse/declaracaomensal/lista-lote.xhtml', (select ID from menu where LABEL = 'NFS-E'), 10);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > DECLARACOES MENSAIS > LISTA',
     '/tributario/nfse/declaracaomensal/lista-lote.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

