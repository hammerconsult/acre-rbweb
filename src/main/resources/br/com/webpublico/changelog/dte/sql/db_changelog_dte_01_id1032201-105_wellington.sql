    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > NOTIFICAÇÃO DO CONTRIBUINTE > LISTA',
           '/tributario/dte/notificacaocontribuinte/lista.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/notificacaocontribuinte/lista.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/notificacaocontribuinte/lista.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/notificacaocontribuinte/lista.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > NOTIFICAÇÃO DO CONTRIBUINTE > VISUALIZAR',
           '/tributario/dte/notificacaocontribuinte/visualizar.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/notificacaocontribuinte/visualizar.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/notificacaocontribuinte/visualizar.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/notificacaocontribuinte/visualizar.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

    INSERT INTO menu (id, label, caminho, pai_id, ordem)
    select HIBERNATE_SEQUENCE.nextval,
           'NOTIFICAÇÃO DO CONTRIBUINTE',
           '/tributario/dte/notificacaocontribuinte/lista.xhtml',
           (select ID from menu where LABEL = 'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO'),
           120
     from dual
    where not exists(select id from menu where CAMINHO = '/tributario/dte/notificacaocontribuinte/lista.xhtml');
