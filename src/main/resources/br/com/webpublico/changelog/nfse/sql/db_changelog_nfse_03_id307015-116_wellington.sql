INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'DES-IF', null,
        (SELECT ID FROM MENU WHERE LABEL = 'ENCERRAMENTO MENSAL DE SERVIÇO' AND CAMINHO IS NULL), 100);

UPDATE MENU
SET PAI_ID = (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL),
    ORDEM  = 5
WHERE LABEL = 'CÓDIGO DE TRIBUTAÇÃO MUNICIPAL';

UPDATE MENU
SET PAI_ID = (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL),
    ORDEM  = 500
WHERE LABEL = 'ARQUIVO DES-IF';


INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > CÓDIGO DE TRIBUTAÇÃO > LISTAR',
        '/tributario/nfse/codigo-tributacao/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > NOTA FISCAL > CÓDIGO DE TRIBUTAÇÃO > VISUALIZAR',
        '/tributario/nfse/codigo-tributacao/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/codigo-tributacao/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/nfse/codigo-tributacao/visualizar.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'CÓDIGO DE TRIBUTAÇÃO',
        '/tributario/nfse/codigo-tributacao/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'DES-IF' AND CAMINHO IS NULL), 0);
