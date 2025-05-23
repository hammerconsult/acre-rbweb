INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES(hibernate_sequence.nextval, 'RECURSOS HUMANOS > RELATÓRIOS - RH > VIDA FUNCIONAL > RELATÓRIO PRÉVIA DE SEXTA PARTE', '/rh/relatorios/relatorioPreviaSextaParte.xhtml',
       0, 'RH');

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'RELATÓRIO PRÉVIA DE SEXTA PARTE',
        '/rh/relatorios/relatorioPreviaSextaParte.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'VIDA FUNCIONAL' AND CAMINHO IS NULL), 310);

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'Recursos Humanos'),
        (SELECT ID FROM RECURSOSISTEMA WHERE NOME = 'RECURSOS HUMANOS > RELATÓRIOS - RH > VIDA FUNCIONAL > RELATÓRIO PRÉVIA DE SEXTA PARTE'));
