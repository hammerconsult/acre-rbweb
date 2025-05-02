
INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (hibernate_sequence.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO TRIBUTÁRIA - ISSQN > AUTO DE INFRAÇÃO > EDITA',
        '/tributario/fiscalizacao/autoinfracao/edita.xhtml',
        0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select distinct grs.GRUPORECURSO_ID,
                (SELECT ID
                 FROM RECURSOSISTEMA
                 WHERE NOME = 'TRIBUTÁRIO > FISCALIZAÇÃO TRIBUTÁRIA - ISSQN > AUTO DE INFRAÇÃO > EDITA')
from GRUPORECURSOSISTEMA grs
         inner join recursosistema rs on grs.RECURSOSISTEMA_ID = rs.ID
where rs.caminho like '/tributario/fiscalizacao/autoinfracao/%';
