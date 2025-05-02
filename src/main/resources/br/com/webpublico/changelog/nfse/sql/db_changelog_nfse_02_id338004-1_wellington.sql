insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > NOTA FISCAL > COMPARATIVO ESTBAN X NFS-E',
        '/tributario/nfse/comparativo-estban-nfse/edita.xhtml', 0, 'TRIBUTARIO');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
values (HIBERNATE_SEQUENCE.nextval, 'COMPARATIVO ESTBAN X NFS-E',
        '/tributario/nfse/comparativo-estban-nfse/edita.xhtml',
        (select id from menu where LABEL = 'DES-IF'),
        600, null);
