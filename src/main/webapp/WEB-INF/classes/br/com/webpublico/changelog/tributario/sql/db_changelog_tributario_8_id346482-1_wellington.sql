INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
SELECT hibernate_sequence.nextval,
       'TRIBUTÁRIO > CONTA CORRENTE > CONSULTA E GERAÇÃO DE RESÍDUO DE ARRECADAÇÃO > EDITA',
       '/tributario/contacorrente/residuoarrecadacao/edita.xhtml',
       0,
       'TRIBUTARIO'
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/contacorrente/residuoarrecadacao/edita.xhtml');

INSERT INTO GRUPORECURSOSISTEMA (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
select gr.id, rs.id
from gruporecurso gr, recursosistema rs
where gr.NOME = 'ADMINISTRADOR TRIBUTÁRIO'
  and rs.CAMINHO = '/tributario/contacorrente/residuoarrecadacao/edita.xhtml'
  and not exists (select 1 from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.ID
                    and grs.recursosistema_id = rs.id);

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.NEXTVAL, 'CONSULTA E GERAÇÃO DE RESÍDUO DE ARRECADAÇÃO',
       '/tributario/contacorrente/residuoarrecadacao/edita.xhtml',
       (SELECT ID FROM MENU WHERE LABEL = 'CONTA CORRENTE' AND CAMINHO IS NULL), 100
from dual
where not exists (select 1 from menu where CAMINHO = '/tributario/contacorrente/residuoarrecadacao/edita.xhtml');
