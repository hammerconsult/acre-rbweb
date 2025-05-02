insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > LOTE DE CANCELAMENTO > LISTA',
        '/tributario/nfse/lote-cancelamento/lista.xhtml',
        0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/lote-cancelamento/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > LOTE DE CANCELAMENTO > VISUALIZAR',
        '/tributario/nfse/lote-cancelamento/visualizar.xhtml',
        0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/lote-cancelamento/visualizar.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > LOTE DE CANCELAMENTO > EDITA',
        '/tributario/nfse/lote-cancelamento/edita.xhtml',
        0, 'TRIBUTARIO' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/lote-cancelamento/edita.xhtml');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'LOTE DE CANCELAMENTO DE NFS-E',
       '/tributario/nfse/lote-cancelamento/lista.xhtml',
       (select id from menu where label = 'NOTA FISCAL'),
       83 from dual
where not exists (select 1 from menu where label = 'LOTE DE CANCELAMENTO DE NFS-E');

