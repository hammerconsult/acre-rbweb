    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > TERMO ADESÃO DTE > LISTA',
           '/tributario/dte/termoadesao/lista.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/termoadesao/lista.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/termoadesao/lista.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/termoadesao/lista.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > TERMO ADESÃO DTE > VISUALIZAR',
           '/tributario/dte/termoadesao/visualizar.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/termoadesao/visualizar.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/termoadesao/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/termoadesao/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > TERMO ADESÃO DTE > EDITAR',
           '/tributario/dte/termoadesao/edita.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/termoadesao/edita.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/termoadesao/edita.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/notificacaocontribuinte/edita.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'TERMO DE ADESÃO - DTE',
           '/tributario/dte/termoadesao/lista.xhtml',
           (select ID from menu where LABEL = 'CADASTROS GERAIS - DTE'),
           0
     from dual
    where not exists(select id from menu where CAMINHO = '/tributario/dte/termoadesao/lista.xhtml');
