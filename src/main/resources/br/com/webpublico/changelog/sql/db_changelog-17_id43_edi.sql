CREATE MATERIALIZED VIEW "VWHIERARQUIAORCAMENTARIA" ("ID", "IDEXERCICIO", "IDUNIDADEORGANIZACIONAL", "SUPERIOR_ID", "SUBORDINADA_ID", "ENTIDADE_ID", "CODIGO", "DESCRICAO", "EXERCICIO_ID", "TIPOUNIDADE", "TIPOADMINISTRACAO", "ESFERADOPODER", "CLASSIFICACAOUO", "INICIOVIGENCIA", "FIMVIGENCIA", "ANO")
AS
  SELECT raiz.id,
    e.id  AS idExercicio,
    uo.id AS idUnidadeOrganizacional,
    raiz.superior_id,
    raiz.subordinada_id,
    ent.id AS entidade,
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
  ON ent.id = recuperaentidadedaorcviaadm(raiz.id, COALESCE(raiz.iniciovigencia, to_date('0101'
    ||e.ano,'ddmmyyyy')), raiz.fimvigencia)
  WHERE RAIZ.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA'