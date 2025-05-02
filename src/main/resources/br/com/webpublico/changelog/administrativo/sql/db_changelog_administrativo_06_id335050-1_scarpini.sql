CREATE OR REPLACE VIEW vwhierarquiaadmgestorlicitacao as
SELECT h.*, uu.USUARIOSISTEMA_ID
FROM vwhierarquiaadministrativa vw
         INNER JOIN hierarquiaorganizacional h ON h.id = vw.id
         INNER JOIN unidadeorganizacional u on u.id = h.subordinada_id
         INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id
WHERE uu.gestorlicitacao = 1
ORDER BY VW.CODIGO;
