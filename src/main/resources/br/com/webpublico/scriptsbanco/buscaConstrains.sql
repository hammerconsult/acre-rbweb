-- busca as colunas com nome exercicio em todas as tabelas.

SELECT
TABLE_NAME,
COLUMN_NAME,
DATA_TYPE
   FROM
      ALL_TAB_COLUMNS
WHERE
 OWNER = 'WEBPUBLICO2'
AND COLUMN_NAME LIKE '%EXERCICIO%'

-- busca constraint

select constraint_name,constraint_type,table_name,r_constraint_name
from all_constraints
where constraint_name = 'FKF7D93CC0DA1C68A8'
