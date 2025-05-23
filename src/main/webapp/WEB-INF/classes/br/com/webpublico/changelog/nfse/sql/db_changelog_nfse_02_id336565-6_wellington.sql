INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
SELECT hibernate_sequence.nextval,
       'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > COMUNICADO > LISTA',
       '/tributario/nfse/notapremiada/comunicado/lista.xhtml',
       0,
       'TRIBUTARIO'
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/notapremiada/comunicado/lista.xhtml');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
SELECT hibernate_sequence.nextval,
       'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > COMUNICADO > EDITA',
       '/tributario/nfse/notapremiada/comunicado/edita.xhtml',
       0,
       'TRIBUTARIO'
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/notapremiada/comunicado/edita.xhtml');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
SELECT hibernate_sequence.nextval,
       'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > COMUNICADO > VISUALIZAR',
       '/tributario/nfse/notapremiada/comunicado/visualizar.xhtml',
       0,
       'TRIBUTARIO'
FROM DUAL
WHERE NOT EXISTS(
        SELECT 1 FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/notapremiada/comunicado/visualizar.xhtml');


INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
SELECT HIBERNATE_SEQUENCE.NEXTVAL,
       'COMUNICADO (NOTA PREMIADA)',
       '/tributario/nfse/notapremiada/comunicado/lista.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'NOTA PREMIADA' AND CAMINHO IS NULL),
       100
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM MENU WHERE CAMINHO = '/tributario/nfse/notapremiada/comunicado/lista.xhtml');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
SELECT (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
       (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/lista.xhtml')
FROM DUAL
WHERE NOT EXISTS(SELECT 1
                 FROM GRUPORECURSOSISTEMA S
                 WHERE S.GRUPORECURSO_ID = (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial')
                   AND S.RECURSOSISTEMA_ID = (SELECT ID
                                              FROM RECURSOSISTEMA
                                              WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/lista.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
SELECT (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
       (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/edita.xhtml')
FROM DUAL
WHERE NOT EXISTS(SELECT 1
                 FROM GRUPORECURSOSISTEMA S
                 WHERE S.GRUPORECURSO_ID = (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial')
                   AND S.RECURSOSISTEMA_ID = (SELECT ID
                                              FROM RECURSOSISTEMA
                                              WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/edita.xhtml'));

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
SELECT (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial'),
       (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/visualizar.xhtml')
FROM DUAL
WHERE NOT EXISTS(SELECT 1
                 FROM GRUPORECURSOSISTEMA S
                 WHERE S.GRUPORECURSO_ID = (SELECT ID FROM GRUPORECURSO WHERE NOME = 'TRB - NFS-e - Gerencial')
                   AND S.RECURSOSISTEMA_ID = (SELECT ID
                                              FROM RECURSOSISTEMA
                                              WHERE CAMINHO LIKE '/tributario/nfse/notapremiada/comunicado/visualizar.xhtml'));
