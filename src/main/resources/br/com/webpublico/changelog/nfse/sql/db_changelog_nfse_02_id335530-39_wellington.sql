delete
from GRUPORECURSOSISTEMA
where RECURSOSISTEMA_ID in (
    select id from recursosistema where caminho like '/tributario/nfse/campanha/%'
);

delete
from recursosistema
where caminho like '/tributario/nfse/campanha/%';

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > SORTEIO > LISTA',
        '/tributario/nfse/sorteio/lista.xhtml', 0, 'TRIBUTARIO');
insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > SORTEIO > EDITA',
        '/tributario/nfse/sorteio/edita.xhtml', 0, 'TRIBUTARIO');
insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > SORTEIO > VISUALIZAR',
        '/tributario/nfse/sorteio/visualizar.xhtml', 0, 'TRIBUTARIO');

insert into GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
select RECURSOSISTEMA.id, GRUPORECURSO.id
from GRUPORECURSO,
     RECURSOSISTEMA
where GRUPORECURSO.NOME = 'ADMINISTRADOR TRIBUTÁRIO'
  and RECURSOSISTEMA.CAMINHO like '/tributario/nfse/sorteio/%';


update menu
set LABEL   = 'SORTEIO NOTA PREMIADA',
    CAMINHO = '/tributario/nfse/sorteio/lista.xhtml'
where caminho like '/tributario/nfse/campanha/lista.xhtml';


