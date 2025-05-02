CREATE OR REPLACE VIEW VWHIERARQUIADESPESAORC AS
SELECT
D.ID as id,
hoUo.CODIGO ||' - '||uoOrg.descricao as orgao,
HO.CODIGO||' - '|| uoUni.descricao as unidade,
F.CODIGO||'.'||SF.CODIGO||'.'||P.CODIGO||'.'||TPA.CODIGO||''||A.CODIGO||'.'||SUB.CODIGO||' - '||SUB.DESCRICAO AS SUBACAO,
con.CODIGO||' - '||con.DESCRICAO AS CONTA,
d.exercicio_id as EXERCICIO
FROM DESPESAORC D
INNER JOIN PROVISAOPPADESPESA PD ON pd.ID = d.provisaoppadespesa_id
INNER JOIN PROVISAOPPA PROV ON PROV.ID = pd.provisaoppa_id
INNER JOIN SUBACAOPPA SUB ON SUB.ID = prov.subacao_id
INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id
INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID
INNER JOIN programappa P ON P.ID = A.programa_id
INNER JOIN FUNCAO F ON F.ID = A.funcao_id
INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id
INNER JOIN unidadeorganizacional uoUni ON uoUni.ID = A.responsavel_id
INNER JOIN hierarquiaorganizacional HO ON ho.subordinada_id = uoUni.ID and ho.EXERCICIO_ID=d.EXERCICIO_ID
AND ho.tipohierarquiaorganizacional = 'ORCAMENTARIA'
INNER JOIN unidadeorganizacional uoOrg ON uoOrg.ID = ho.superior_id
INNER JOIN hierarquiaorganizacional hoUo ON hoUo.subordinada_id = uoOrg.ID and hoUo.EXERCICIO_ID =d.EXERCICIO_ID
AND hoUo.tipohierarquiaorganizacional = 'ORCAMENTARIA'
INNER JOIN CONTA con on con.ID = PD.CONTADEDESPESA_ID
order by D.CODIGO;