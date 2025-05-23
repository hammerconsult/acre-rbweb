insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > COMPARATIVO SIMPLES NACIONAL X NFS-E > LISTAR',
        '/tributario/nfse/comparativo-simples-nacional-nota-fiscal/lista.xhtml', 0,
        'NFSE');

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > COMPARATIVO SIMPLES NACIONAL X NFS-E > EDITAR',
        '/tributario/nfse/comparativo-simples-nacional-nota-fiscal/edita.xhtml', 0,
        'NFSE');

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > COMPARATIVO SIMPLES NACIONAL X NFS-E > VISUALIZAR',
        '/tributario/nfse/comparativo-simples-nacional-nota-fiscal/visualizar.xhtml', 0,
        'NFSE');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
values (HIBERNATE_SEQUENCE.nextval, 'COMPARATIVO SIMPLES NACIONAL X NFS-E',
        '/tributario/nfse/comparativo-simples-nacional-nota-fiscal/lista.xhtml',
        (select id from menu where LABEL = 'NOTA FISCAL'),
        500);
