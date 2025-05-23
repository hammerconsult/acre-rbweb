delete
from GRUPORECURSOSISTEMA
where RECURSOSISTEMA_ID in (
    select id from recursosistema where caminho like '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml'
);

delete
from recursosistema
where caminho like '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml';

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NOTA FISCAL > NOTA PREMIADA > RECLAMAÇÕES > LISTA',
        '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml', 0, 'TRIBUTARIO');


insert into GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
select RECURSOSISTEMA.id, GRUPORECURSO.id
from GRUPORECURSO,
     RECURSOSISTEMA
where GRUPORECURSO.NOME in ('ADMINISTRADOR TRIBUTÁRIO', 'TRB - NFS-e - Gerencial', 'Tributário (MGA)','TRB - ISSQN Gerencial')
  and RECURSOSISTEMA.CAMINHO like '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml';


INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'RECLAMAÇÕES',
        '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'NOTA PREMIADA' AND CAMINHO IS NULL), 50);


