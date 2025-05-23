INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'DECLARACOES MENSAIS',
                         '/tributario/nfse/declaracaomensal/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 10);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'LANÇAMENTO GERAL DE DECLARACOES',
                         '/tributario/nfse/declaracaomensal/lancamento-geral.xhtml', (select ID from menu where LABEL = 'NFS-E'), 10);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > DECLARACOES MENSAIS > LISTA',
     '/tributario/nfse/declaracaomensal/lista.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > DECLARACOES MENSAIS  > VISUALIZA',
     '/tributario/nfse/declaracaomensal/visualizar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > DECLARACOES MENSAIS  > LANÇAMENTO GERAL',
     '/tributario/nfse/declaracaomensal/lancamento-geral.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

