INSERT INTO menu (ID,
                  LABEL,
                  CAMINHO,
                  PAI_ID,
                  ORDEM,
                  ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'CONFÊRENCIA DE ENVIADOS AO ESOCIAL',
        '/rh/relatorios/relatorioenviadosesocial.xhtml',
        (SELECT ID FROM menu WHERE LABEL = 'E-SOCIAL'),
        (SELECT max(ORDEM) + 10 FROM menu WHERE PAI_ID = (SELECT ID FROM menu WHERE LABEL = 'E-SOCIAL')),
        NULL);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval, 'RECURSOS HUMANOS > E-SOCIAL > CONFÊRENCIA DE ENVIADOS AO ESOCIAL',
        '/rh/relatorios/relatorioenviadosesocial.xhtml', 0, 'RH');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, (SELECT gr.ID
                                     FROM GRUPORECURSO gr
                                     WHERE MODULOSISTEMA = 'RH'
                                       AND UPPER(NOME) = UPPER('Recursos Humanos')));
