    INSERT INTO RECURSOSISTEMA
    select HIBERNATE_SEQUENCE.nextval,
           'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > EMISSÃO NOTIFICAÇÃO > ACOMPANHA',
           '/tributario/dte/emissaonotificacao/acompanha.xhtml',
           0,
           'TRIBUTARIO'
    from dual
    where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/emissaonotificacao/acompanha.xhtml');

    INSERT INTO GRUPORECURSOSISTEMA
    SELECT (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/acompanha.xhtml'),
           (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO') FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM GRUPORECURSOSISTEMA
                      WHERE RECURSOSISTEMA_ID = (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/dte/emissaonotificacao/acompanha.xhtml')
                        AND GRUPORECURSO_ID = (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));
