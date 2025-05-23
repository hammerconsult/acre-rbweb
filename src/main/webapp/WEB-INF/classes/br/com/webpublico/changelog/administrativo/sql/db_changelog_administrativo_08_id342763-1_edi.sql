INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'LICITAÇÃO > COMPRAS E LICITAÇÕES > INTEGRAÇÃO - PNCP',
        '/administrativo/licitacao/pncp/integracao-pncp.xhtml', 0, 'LICITACAO');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval, 'INTEGRAÇÃO - PNCP',
        '/administrativo/licitacao/pncp/integracao-pncp.xhtml',
        (select ID from menu where LABEL = 'COMPRAS E LICITAÇÕES'),
        (select max(ORDEM) + 2 from menu where LABEL = 'CADASTROS GERAIS - LICITAÇÃO'), null);
