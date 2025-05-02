    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > MODELO DE DOCUMENTO > LISTA',
           '/tributario/dte/modelodocumento/lista.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/modelodocumento/lista.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/lista.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/lista.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > MODELO DE DOCUMENTO > VISUALIZAR',
           '/tributario/dte/modelodocumento/visualizar.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/modelodocumento/visualizar.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > MODELO DE DOCUMENTO > EDITA',
           '/tributario/dte/modelodocumento/edita.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/modelodocumento/edita.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/edita.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/modelodocumento/edita.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > GRUPO DE CONTRIBUINTE > LISTA',
           '/tributario/dte/grupocontribuinte/lista.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/grupocontribuinte/lista.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/lista.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/lista.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > GRUPO DE CONTRIBUINTE > VISUALIZAR',
           '/tributario/dte/grupocontribuinte/visualizar.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/grupocontribuinte/visualizar.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > GRUPO DE CONTRIBUINTE > EDITA',
           '/tributario/dte/grupocontribuinte/edita.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/grupocontribuinte/edita.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/edita.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/grupocontribuinte/edita.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'CADASTROS GERAIS - DTE',
           null,
           (select ID from menu where LABEL = 'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO'),
           0
     from dual
    where not exists(select id from menu where label = 'CADASTROS GERAIS - DTE');

    update menu set pai_id = (select id from menu where label = 'CADASTROS GERAIS - DTE') where caminho = '/tributario/dte/configuracao/configurar.xhtml';

    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'MODELO DE DOCUMENTO - DTE',
           '/tributario/dte/modelodocumento/lista.xhtml',
           (select ID from menu where LABEL = 'CADASTROS GERAIS - DTE'),
           10
     from dual
    where not exists(select id from menu where CAMINHO = '/tributario/dte/modelodocumento/lista.xhtml');

    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'GRUPO DE CONTRIBUINTE - DTE',
           '/tributario/dte/grupocontribuinte/lista.xhtml',
           (select ID from menu where LABEL = 'CADASTROS GERAIS - DTE'),
           10
     from dual
    where not exists(select id from menu where CAMINHO = '/tributario/dte/grupocontribuinte/lista.xhtml');
