  CREATE OR REPLACE FORCE VIEW VWHIERARQUIAADMINISTRATIVA (ID,
    SUPERIOR_ID, SUBORDINADA_ID, ENTIDADE_ID, CODIGO, DESCRICAO,
    EXERCICIO_ID, TIPOUNIDADE, TIPOADMINISTRACAO, ESFERADOPODER,
    CLASSIFICACAOUO, INICIOVIGENCIA, FIMVIGENCIA, ANO)
  AS
  WITH
    REC (ID,SUPERIOR_ID, SUBORDINADA_ID, ENTIDADE_ID, CODIGO, DESCRICAO,
    EXERCICIO_ID, TIPOUNIDADE, TIPOADMINISTRACAO, ESFERADOPODER, CLASSIFICACAOUO,
    INICIOVIGENCIA, FIMVIGENCIA, ANO ) AS
    (
      SELECT
        RAIZ.ID,
        RAIZ.SUPERIOR_ID,
        RAIZ.SUBORDINADA_ID,
        ENT.ID,
        RAIZ.CODIGO,
        RAIZ.DESCRICAO,
        RAIZ.EXERCICIO_ID,
        ENT.TIPOUNIDADE,
        ENT.TIPOADMINISTRACAO,
        ENT.ESFERADOPODER,
        ENT.CLASSIFICACAOUO,
        COALESCE(RAIZ.INICIOVIGENCIA, TO_DATE('0101'
        ||E.ANO,'ddmmyyyy')) AS INICIOVIGENCIA,
        RAIZ.FIMVIGENCIA,
        E.ANO
      FROM
        HIERARQUIAORGANIZACIONAL RAIZ
      INNER JOIN EXERCICIO E
      ON
        E.ID = RAIZ.EXERCICIO_ID
      INNER JOIN UNIDADEORGANIZACIONAL UO
      ON
        UO.ID = RAIZ.SUBORDINADA_ID
      LEFT JOIN ENTIDADE ENT
      ON
        ENT.ID = UO.ENTIDADE_ID
      WHERE
        RAIZ.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA'
      AND RAIZ.SUPERIOR_ID               IS NULL
      UNION ALL
      SELECT
        FILHO.ID,
        FILHO.SUPERIOR_ID,
        FILHO.SUBORDINADA_ID,
        CASE
          WHEN
            (
              ENT.ID IS NULL
            )
          THEN REC.ENTIDADE_ID
          ELSE ENT.ID
        END AS ID,
        FILHO.CODIGO,
        FILHO.DESCRICAO,
        FILHO.EXERCICIO_ID,
        CASE
          WHEN
            (
              ENT.TIPOUNIDADE IS NULL
            )
          THEN ENT.TIPOUNIDADE
          ELSE REC.TIPOUNIDADE
        END,
        CASE
          WHEN
            (
              ENT.TIPOADMINISTRACAO IS NULL
            )
          THEN ENT.TIPOADMINISTRACAO
          ELSE REC.TIPOADMINISTRACAO
        END,
        CASE
          WHEN
            (
              ENT.ESFERADOPODER IS NOT NULL
            )
          THEN ENT.ESFERADOPODER
          ELSE REC.ESFERADOPODER
        END,
        CASE
          WHEN
            (
              ENT.CLASSIFICACAOUO IS NOT NULL
            )
          THEN ENT.CLASSIFICACAOUO
          ELSE REC.CLASSIFICACAOUO
        END,
        COALESCE(FILHO.INICIOVIGENCIA, TO_DATE('0101'
        ||E.ANO,'ddmmyyyy')) AS INICIOVIGENCIA,
        FILHO.FIMVIGENCIA,
        E.ANO
      FROM
        HIERARQUIAORGANIZACIONAL FILHO
      INNER JOIN REC
      ON
        REC.SUBORDINADA_ID = FILHO.SUPERIOR_ID
      AND FILHONAVIGENCIA(FILHO.INICIOVIGENCIA, FILHO.FIMVIGENCIA,
        REC.INICIOVIGENCIA, REC.FIMVIGENCIA) = 1
      INNER JOIN EXERCICIO E
      ON
        E.ID = FILHO.EXERCICIO_ID
      INNER JOIN UNIDADEORGANIZACIONAL UO
      ON
        UO.ID = FILHO.SUBORDINADA_ID
      LEFT JOIN ENTIDADE ENT
      ON
        ENT.ID = UO.ENTIDADE_ID
      WHERE
        FILHO.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA'
    )
  SELECT DISTINCT
    ID,
    SUPERIOR_ID,
    SUBORDINADA_ID,
    ENTIDADE_ID,
    CODIGO,
    DESCRICAO,
    EXERCICIO_ID,
    TIPOUNIDADE,
    TIPOADMINISTRACAO,
    ESFERADOPODER,
    CLASSIFICACAOUO,
    INICIOVIGENCIA,
    FIMVIGENCIA,
    ANO
  FROM
    REC