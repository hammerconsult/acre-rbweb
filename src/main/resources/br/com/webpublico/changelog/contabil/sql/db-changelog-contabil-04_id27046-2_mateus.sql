CREATE MATERIALIZED VIEW VWHIERARQUIAORCAMENTARIA (ID, IDEXERCICIO, IDUNIDADEORGANIZACIONAL, SUPERIOR_ID, SUBORDINADA_ID, ENTIDADE_ID, CODIGO, DESCRICAO, EXERCICIO_ID, TIPOUNIDADE, TIPOADMINISTRACAO, ESFERADOPODER, CLASSIFICACAOUO, INICIOVIGENCIA, FIMVIGENCIA, ANO)
  AS
    SELECT raiz.id,
      e.id  AS idExercicio,
      raiz.subordinada_id AS idUnidadeOrganizacional,
      raiz.superior_id,
      raiz.subordinada_id,
      ent.id AS entidade,
      raiz.codigo,
      raiz.descricao,
      raiz.exercicio_id,
      ent.TIPOUNIDADE,
      ent.tipoadministracao,
      ent.esferadopoder,
      ent.classificacaouo,
      trunc(raiz.iniciovigencia) AS iniciovigencia,
      trunc(raiz.fimvigencia) as fimvigencia,
      e.ano
    FROM hierarquiaorganizacional raiz
    INNER JOIN exercicio e ON e.id = raiz.exercicio_id
    LEFT JOIN entidade ent ON ent.id = recuperaentidadedaorcviaadm(raiz.id, trunc(raiz.iniciovigencia), trunc(RAIZ.FIMVIGENCIA))
    WHERE RAIZ.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA'
