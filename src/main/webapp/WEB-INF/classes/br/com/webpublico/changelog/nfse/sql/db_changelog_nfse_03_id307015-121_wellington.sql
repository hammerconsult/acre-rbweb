INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > TIPO DEPENDÊNCIA DES-IF > LISTAR',
        '/tributario/nfse/tipo-dependencia-desif/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > TIPO DEPENDÊNCIA DES-IF > VISUALIZAR',
        '/tributario/nfse/tipo-dependencia-desif/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/tipo-dependencia-desif/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/tipo-dependencia-desif/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'TIPO DEPENDÊNCIA DES-IF',
        '/tributario/nfse/tipo-dependencia-desif/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 30);
