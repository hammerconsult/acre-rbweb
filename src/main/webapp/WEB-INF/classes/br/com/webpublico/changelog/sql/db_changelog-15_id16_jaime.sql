CREATE OR REPLACE FORCE VIEW "TESTE"."VWHIERARQUIAADMINISTRATIVA" ("ID", "SUPERIOR_ID", "SUBORDINADA_ID", "ENTIDADE_ID", "CODIGO", "DESCRICAO", "EXERCICIO_ID", "TIPOUNIDADE", "TIPOADMINISTRACAO", "ESFERADOPODER", "CLASSIFICACAOUO", "INICIOVIGENCIA", "FIMVIGENCIA", "ANO")
                                                                                                                                                                                           AS
WITH rec (id,superior_id, subordinada_id, entidade_id, codigo, descricao, exercicio_id, TIPOUNIDADE, tipoadministracao, esferadopoder, classificacaouo, iniciovigencia, fimvigencia, ano ) AS
  (SELECT raiz.id,
    raiz.superior_id,
    raiz.subordinada_id,
    ent.id,
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
  WHERE raiz.tipohierarquiaorganizacional = 'ADMINISTRATIVA'
  AND raiz.superior_id                   IS NULL
  UNION ALL
  SELECT filho.id,
    filho.superior_id,
    filho.subordinada_id,
    CASE
      WHEN (ENT.ID IS NULL)
      THEN REC.ENTIDADE_ID
      ELSE ENT.ID
    END AS id,
    filho.codigo,
    uo.descricao,
    filho.exercicio_id,
    CASE
      WHEN (ent.tipounidade IS NULL)
      THEN ent.TIPOUNIDADE
      ELSE rec.tipounidade
    END,
    CASE
      WHEN (ent.tipoadministracao IS NULL)
      THEN ent.tipoadministracao
      ELSE rec.tipoadministracao
    END,
    CASE
      WHEN (ent.esferadopoder IS NOT NULL)
      THEN ent.esferadopoder
      ELSE rec.esferadopoder
    END,
    CASE
      WHEN (ent.classificacaouo IS NOT NULL)
      THEN ent.classificacaouo
      ELSE rec.classificacaouo
    END,
    COALESCE(filho.iniciovigencia, to_date('0101'
    ||e.ano,'ddmmyyyy')) AS iniciovigencia,
    filho.fimvigencia,
    e.ano
  FROM hierarquiaorganizacional filho
  INNER JOIN rec
  ON rec.subordinada_id                                                                             = filho.superior_id
  AND filhoNaVigencia(filho.iniciovigencia, filho.fimvigencia, rec.iniciovigencia, rec.fimvigencia) = 1
  INNER JOIN exercicio e
  ON e.id = filho.exercicio_id
  INNER JOIN unidadeorganizacional uo
  ON uo.id = filho.subordinada_id
  LEFT JOIN entidade ent
  ON ent.id                                = uo.entidade_id
  WHERE filho.tipohierarquiaorganizacional = 'ADMINISTRATIVA'
  )
SELECT "ID",
  "SUPERIOR_ID",
  "SUBORDINADA_ID",
  "ENTIDADE_ID",
  "CODIGO",
  "DESCRICAO",
  "EXERCICIO_ID",
  "TIPOUNIDADE",
  "TIPOADMINISTRACAO",
  "ESFERADOPODER",
  "CLASSIFICACAOUO",
  "INICIOVIGENCIA",
  "FIMVIGENCIA",
  "ANO"
FROM rec;