INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'RELATÓRIOS',
                         NULL, -126757915, 120);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'AUDITORIA DOS CADASTROS',
                         '/tributario/relatorio/auditoriacadastro/auditoria-cadastro.xhtml',(select ID from menu where LABEL = 'RELATÓRIOS' AND MENU.PAI_ID = -126757915), 1);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'RELATÓRIO DE AUDITORIA DOS CADASTROS',
     '/tributario/relatorio/auditoriacadastro/auditoria-cadastro.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);


