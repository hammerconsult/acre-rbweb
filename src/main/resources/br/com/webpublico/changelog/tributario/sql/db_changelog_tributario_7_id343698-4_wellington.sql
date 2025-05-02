insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'NFSE > DFE-NACIONAL > LISTA',
        '/tributario/nfse/dfe-nacional/lista.xhtml', 0, 'NFSE' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/dfe-nacional/lista.xhtml');

insert into recursosistema (ID, NOME, CAMINHO, CADASTRO, MODULO)
select HIBERNATE_SEQUENCE.nextval, 'NFSE > DFE-NACIONAL > VISUALIZAR',
       '/tributario/nfse/dfe-nacional/visualizar.xhtml', 0, 'NFSE' from dual
where not exists (select 1 from recursosistema where caminho = '/tributario/nfse/dfe-nacional/visualizar.xhtml');

insert into menu (ID, LABEL, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'NOTA FISCAL NACIONAL',
        (select id from menu where LABEL = 'NOTA FISCAL'),
        (select max(ordem) from menu
         where pai_id = (select id from menu where label = 'NOTA FISCAL')) + 10 from dual
where not exists (select 1 from menu where label = 'NOTA FISCAL NACIONAL');

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'DF-E',
       '/tributario/nfse/dfe-nacional/lista.xhtml',
       (select id from menu where LABEL = 'NOTA FISCAL NACIONAL'),
        0 from dual
where not exists (select 1 from menu where label = 'NOTA FISCAL NACIONAL');

insert into GRUPORECURSOSISTEMA (recursosistema_id, gruporecurso_id)
select (select id from recursosistema where caminho = '/tributario/nfse/dfe-nacional/lista.xhtml'),
       (select id from gruporecurso where NOME = 'ADMINISTRADOR TRIBUTÁRIO')
       from dual
where not exists (select 1 from gruporecursosistema grs
                  where grs.RECURSOSISTEMA_ID = (select id from recursosistema where caminho = '/tributario/nfse/dfe-nacional/lista.xhtml')
                    and grs.GRUPORECURSO_ID = (select id from gruporecurso where NOME = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into GRUPORECURSOSISTEMA (recursosistema_id, gruporecurso_id)
select (select id from recursosistema where caminho = '/tributario/nfse/dfe-nacional/visualizar.xhtml'),
       (select id from gruporecurso where NOME = 'ADMINISTRADOR TRIBUTÁRIO')
from dual
where not exists (select 1 from gruporecursosistema grs
                  where grs.RECURSOSISTEMA_ID = (select id from recursosistema where caminho = '/tributario/nfse/dfe-nacional/visualizar.xhtml')
                    and grs.GRUPORECURSO_ID = (select id from gruporecurso where NOME = 'ADMINISTRADOR TRIBUTÁRIO'));
