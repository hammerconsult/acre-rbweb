INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > CÓDIGO DE TRIBUTAÇÃO MUNICIPAL > LISTAR',
        '/tributario/nfse/codigo-tributacao-municipal/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > CÓDIGO DE TRIBUTAÇÃO MUNICIPAL > EDITAR',
        '/tributario/nfse/codigo-tributacao-municipal/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > CÓDIGO DE TRIBUTAÇÃO MUNICIPAL > VISUALIZAR',
        '/tributario/nfse/codigo-tributacao-municipal/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/codigo-tributacao-municipal/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/codigo-tributacao-municipal/edita.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/codigo-tributacao-municipal/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'CÓDIGO DE TRIBUTAÇÃO MUNICIPAL',
        '/tributario/nfse/codigo-tributacao-municipal/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'NOTA FISCAL' AND CAMINHO IS NULL), 100);
