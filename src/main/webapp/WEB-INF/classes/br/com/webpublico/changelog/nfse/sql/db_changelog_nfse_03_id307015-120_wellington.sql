INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > TIPO INSTITUIÇÃO FINANCEIRA > LISTAR',
        '/tributario/nfse/tipo-instituicao-financeira/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > TIPO INSTITUIÇÃO FINANCEIRA > VISUALIZAR',
        '/tributario/nfse/tipo-instituicao-financeira/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/tipo-instituicao-financeira/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/tipo-instituicao-financeira/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'TIPO INSTITUIÇÃO FINANCEIRA',
        '/tributario/nfse/tipo-instituicao-financeira/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 25);

