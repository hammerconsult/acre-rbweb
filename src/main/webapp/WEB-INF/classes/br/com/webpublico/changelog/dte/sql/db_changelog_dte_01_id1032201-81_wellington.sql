    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > EMISSÃO NOTIFICAÇÃO > LISTA',
           '/tributario/dte/emissaonotificacao/lista.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/emissaonotificacao/lista.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/lista.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/lista.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > EMISSÃO NOTIFICAÇÃO > VISUALIZAR',
           '/tributario/dte/emissaonotificacao/visualizar.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/emissaonotificacao/visualizar.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));



    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > EMISSÃO NOTIFICAÇÃO > EDITA',
           '/tributario/dte/emissaonotificacao/edita.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/emissaonotificacao/edita.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'EMISSÃO DE NOTIFICAÇÃO',
           '/tributario/dte/emissaonotificacao/lista.xhtml',
           (select ID from menu where LABEL = 'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO'),
           100
     from dual
    where not exists(select id from menu where CAMINHO = '/tributario/dte/emissaonotificacao/lista.xhtml');

