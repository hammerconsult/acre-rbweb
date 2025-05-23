insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > CAMPANHA > LISTA',
        '/tributario/nfse/campanha/lista.xhtml', 0, 'TRIBUTARIO');
insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > CAMPANHA > EDITA',
        '/tributario/nfse/campanha/edita.xhtml', 0, 'TRIBUTARIO');
insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > CAMPANHA > VISUALIZAR',
        '/tributario/nfse/campanha/visualizar.xhtml', 0, 'TRIBUTARIO');

insert into GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
select RECURSOSISTEMA.id, GRUPORECURSO.id
from GRUPORECURSO,
     RECURSOSISTEMA
where GRUPORECURSO.NOME = 'ADMINISTRADOR TRIBUTÁRIO'
  and RECURSOSISTEMA.CAMINHO like '/tributario/nfse/campanha/%';

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
values (HIBERNATE_SEQUENCE.nextval, 'CAMPANHA NOTA PREMIADA',
        '/tributario/nfse/campanha/lista.xhtml',
        (select id from menu where LABEL = 'NOTA PREMIADA'),
        30);
