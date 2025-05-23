INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > EVENTO CONTÁBIL DES-IF > LISTAR',
        '/tributario/nfse/evento-contabil-desif/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > EVENTO CONTÁBIL DES-IF > VISUALIZAR',
        '/tributario/nfse/evento-contabil-desif/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/evento-contabil-desif/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/evento-contabil-desif/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'EVENTO CONTÁBIL DES-IF',
        '/tributario/nfse/evento-contabil-desif/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 10);
