 INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Cadastro Geral > Usuário Web > Edita', '/comum/usuarioweb/edita.xhtml', 0, 'CADASTROS');
            INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Cadastro Geral - Usuário Web > Lista', '/comum/usuarioweb/lista.xhtml', 0, 'CADASTROS');
            INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Cadastro Geral - Usuário Web > Visualizar', '/comum/usuarioweb/visualizar.xhtml', 0, 'CADASTROS');
 INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
            SELECT RS.ID, GR.ID
            FROM RECURSOSISTEMA RS
            INNER JOIN GRUPORECURSO GR ON GR.NOME IN ('Recursos Humanos', 'ADMINISTRADOR TRIBUTÁRIO')
            WHERE RS.CAMINHO LIKE '/comum/usuarioweb/%';
 INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
            SELECT hibernate_sequence.nextVal, 'USUÁRIO WEB', '/comum/usuarioweb/lista.xhtml', (SELECT ID FROM MENU WHERE LABEL = 'CADASTROS GERAIS'), 255
            FROM DUAL;
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Tributário > NFSE > Plano de Contas Interno > Lista', '/tributario/nfse/plano-contas-interno/lista.xhtml', 0, 'TRIBUTARIO');
            INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Tributário > NFSE > Plano de Contas Interno > Edita', '/tributario/nfse/plano-contas-interno/edita.xhtml', 0, 'TRIBUTARIO');
            INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
            VALUES(hibernate_sequence.nextval, 'Tributário > NFSE > Plano de Contas Interno > Visualizar', '/tributario/nfse/plano-contas-interno/visualizar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
            SELECT RS.ID, GR.ID
            FROM RECURSOSISTEMA RS
            INNER JOIN GRUPORECURSO GR ON GR.NOME IN ('ADMINISTRADOR TRIBUTÁRIO')
            WHERE RS.CAMINHO LIKE '/tributario/nfse/plano-contas-interno/%';
INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
        SELECT hibernate_sequence.nextVal, 'PLANO DE CONTAS INTERNO', '/tributario/nfse/plano-contas-interno/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'NFS-E'), 55
        FROM DUAL;
