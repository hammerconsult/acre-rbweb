CREATE OR REPLACE FORCE VIEW VWHIERARQUIADESPESAORC (ID, ORGAO, UNIDADE, SUBACAO, CONTA, EXERCICIO) AS 
SELECT D.ID AS id,
   hoUo.CODIGO
   ||' - '
   ||uoOrg.descricao AS orgao,
   HO.CODIGO
   ||' - '
   || uoUni.descricao AS unidade,
   F.CODIGO
   ||'.'
   ||SF.CODIGO
   ||'.'
   ||P.CODIGO
   ||'.'
   ||TPA.CODIGO
   ||''
   ||A.CODIGO
   ||'.'
   ||SUB.CODIGO
   ||' - '
   ||SUB.DESCRICAO AS SUBACAO,
   con.CODIGO
   ||' - '
   ||con.DESCRICAO AS CONTA,
   d.exercicio_id  AS EXERCICIO
 FROM DESPESAORC D
 INNER JOIN PROVISAOPPADESPESA PD
 ON pd.ID = d.provisaoppadespesa_id
 INNER JOIN PROVISAOPPA PROV
 ON PROV.ID = pd.provisaoppa_id
 INNER JOIN SUBACAOPPA SUB
 ON SUB.ID = prov.subacao_id
 INNER JOIN ACAOPPA A
 ON A.ID = sub.acaoppa_id
 INNER JOIN TIPOACAOPPA TPA
 ON TPA.ID = A.TIPOACAOPPA_ID
 INNER JOIN programappa P
 ON P.ID = A.programa_id
 INNER JOIN FUNCAO F
 ON F.ID = A.funcao_id
 INNER JOIN SUBFUNCAO SF
 ON SF.ID = A.subfuncao_id
 INNER JOIN unidadeorganizacional uoUni
 ON uoUni.ID = A.responsavel_id
 INNER JOIN hierarquiaorganizacional HO
 ON ho.subordinada_id                = uoUni.ID
 AND ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' and ho.exercicio_id = d.exercicio_id
 INNER JOIN unidadeorganizacional uoOrg
 ON uoOrg.ID = ho.superior_id
 INNER JOIN hierarquiaorganizacional hoUo
 ON hoUo.subordinada_id                = uoOrg.ID
 AND hoUo.tipohierarquiaorganizacional = 'ORCAMENTARIA' and hoUo.exercicio_id = d.exercicio_id
 INNER JOIN CONTA con
 ON con.ID = PD.CONTADEDESPESA_ID
 ORDER BY D.CODIGO ;