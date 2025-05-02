INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > PRODUTO SERVIÇO BANCÁRIO > LISTAR',
        '/tributario/nfse/produto-servico-bancario/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > PRODUTO SERVIÇO BANCÁRIO > VISUALIZAR',
        '/tributario/nfse/produto-servico-bancario/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/produto-servico-bancario/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/produto-servico-bancario/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'PRODUTO SERVIÇO BANCÁRIO',
        '/tributario/nfse/produto-servico-bancario/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 15);
