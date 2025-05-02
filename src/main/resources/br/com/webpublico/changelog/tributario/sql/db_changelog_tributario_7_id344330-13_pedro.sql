INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > ITBI > SOLICITACAO DE ITBI > VISUALIZAR',
        '/tributario/itbi/solicitacaoitbionline/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > ITBI > SOLICITACAO DE ITBI',
        '/tributario/itbi/solicitacaoitbionline/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > ITBI > SOLICITACAO DE ITBI > EDITA',
        '/tributario/itbi/solicitacaoitbionline/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'SOLICITAÇÃO DE ITBI', '/tributario/itbi/solicitacaoitbionline/lista.xhtml',
        (SELECT id
         FROM MENU
         WHERE LABEL = 'ITBI'), 45, NULL);

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES ((SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/itbi/solicitacaoitbionline/lista.xhtml'), (SELECT ID
                                                                                 FROM GRUPORECURSO
                                                                                 WHERE MODULOSISTEMA = 'TRIBUTARIO'
                                                                                   AND NOME LIKE 'ADMINISTRADOR_GERAL'));

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES ((SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/itbi/solicitacaoitbionline/visualizar.xhtml'), (SELECT ID
                                                                                      FROM GRUPORECURSO
                                                                                      WHERE MODULOSISTEMA = 'TRIBUTARIO'
                                                                                        AND NOME LIKE 'ADMINISTRADOR_GERAL'));

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES ((SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/itbi/solicitacaoitbionline/edita.xhtml'), (SELECT ID
                                                                                 FROM GRUPORECURSO
                                                                                 WHERE MODULOSISTEMA = 'TRIBUTARIO'
                                                                                   AND NOME LIKE 'ADMINISTRADOR_GERAL'));
