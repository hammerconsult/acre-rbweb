INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > ALVARÁ > LANÇAMENTO DE TAXAS EM LOTE > EDITA',
        '/tributario/alvara/lancamento-taxas-em-lote/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO MENU
VALUES (HIBERNATE_SEQUENCE.nextval, 'LANÇAMENTO DE TAXAS DE ALVARÁS VIGENTES EM LOTE',
        '/tributario/alvara/lancamento-taxas-em-lote/edita.xhtml',
        (select ID
         from menu
         where LABEL = 'CÁLCULOS'
           and PAI_ID = (SELECT ID FROM MENU WHERE LABEL = 'ALVARÁ')),
        (select MAX(ORDEM) + 10
         from menu
         where LABEL = 'CÁLCULOS'
           and PAI_ID = (SELECT ID FROM MENU WHERE LABEL = 'ALVARÁ')),
        null);
