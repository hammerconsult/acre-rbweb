INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.nextval, 'RELATÓRIO DE SERVIDORES POR VERBA',
        '/rh/relatorios/relatorioservidoresporverba.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'RELATÓRIOS DE CRITICAS'),
        1);
