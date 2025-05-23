INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > PLANO GERAL CONTAS COMENTADO > LISTAR',
        '/tributario/nfse/plano-geral-contas-comentado/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'PLANO GERAL CONTAS COMENTADO',
        '/tributario/nfse/plano-geral-contas-comentado/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 0);
