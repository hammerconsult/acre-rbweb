insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > RELATÓRIOS DA NFSE > RBT12',
        '/tributario/nfse/relatorio/rbt12.xhtml', 0, 'NFSE');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
values (HIBERNATE_SEQUENCE.nextval, 'RELATÓRIO DE RBT12',
        '/tributario/nfse/relatorio/rbt12.xhtml',
        892158256, 300);
