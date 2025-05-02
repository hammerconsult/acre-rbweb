CREATE OR REPLACE FORCE VIEW "VWHIERARQUIAORCAMENTARIA" ("ID", "SUPERIOR_ID", "SUBORDINADA_ID", "ENTIDADE_ID", "CODIGO", "DESCRICAO", "EXERCICIO_ID", "TIPOUNIDADE", "TIPOADMINISTRACAO", "ESFERADOPODER", "CLASSIFICACAOUO", "INICIOVIGENCIA", "FIMVIGENCIA", "ANO")
AS
  SELECT raiz.id,
    raiz.superior_id,
    raiz.subordinada_id,
    recuperaentidadedaorcviaadm(raiz.id, COALESCE(raiz.iniciovigencia, to_date('0101'
    ||e.ano,'ddmmyyyy')), raiz.fimvigencia) AS entidade,
    raiz.codigo,
    uo.descricao,
    raiz.exercicio_id,
    ent.TIPOUNIDADE,
    ent.tipoadministracao,
    ent.esferadopoder,
    ent.classificacaouo,
    COALESCE(raiz.iniciovigencia, to_date('0101'
    ||e.ano,'ddmmyyyy')) AS iniciovigencia,
    raiz.fimvigencia,
    e.ano
  FROM hierarquiaorganizacional raiz
  INNER JOIN exercicio e
  ON e.id = raiz.exercicio_id
  INNER JOIN unidadeorganizacional uo
  ON uo.id = raiz.subordinada_id
  LEFT JOIN entidade ent
  ON ent.id                               = uo.entidade_id
  WHERE raiz.tipohierarquiaorganizacional = 'ORCAMENTARIA';