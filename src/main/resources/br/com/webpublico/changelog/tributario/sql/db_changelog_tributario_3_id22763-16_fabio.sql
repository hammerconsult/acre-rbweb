create or replace FUNCTION get_situacao_parcela_por_data(PARAM_ID_PARCELA    IN NUMBER, param_referencia in TIMESTAMP)
  RETURN NUMBER
AS
  SITUACAO_ID             NUMBER;
BEGIN
select coalesce((select id from (SELECT id
  FROM SITUACAOPARCELAVALORDIVIDA
 WHERE PARCELA_ID = PARAM_ID_PARCELA
   and datalancamento <= param_referencia
   order by DATALANCAMENTO desc, id desc) where rownum = 1),
(select id from (SELECT id
  FROM SITUACAOPARCELAVALORDIVIDA
 WHERE PARCELA_ID = PARAM_ID_PARCELA
order by DATALANCAMENTO asc, id asc) where rownum = 1)) as idSituacao
INTO SITUACAO_ID
from dual;
RETURN SITUACAO_ID;
END get_situacao_parcela_por_data;
