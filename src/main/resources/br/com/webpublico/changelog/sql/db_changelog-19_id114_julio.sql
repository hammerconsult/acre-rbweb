  CREATE OR REPLACE FORCE VIEW VWBEM (BEM_ID, IDENTIFICACAO_PATRIMONIAL, DESCRICAO, VALORORIGINAL, VALORACUMULADODAAMORTIZACAO, VALORACUMULADODADEPRECIACAO, VALORACUMULADODAEXAUSTAO, VALORACUMULADODEAJUSTE, ID_ULTIMO_ESTADO_BEM, ID_ULTIMO_EVENTO_BEM, DATAAQUISICAO)
  AS
    (SELECT b.id      AS bem_id,
      b.identificacao AS identificacao_patrimonial,
      b.descricao,
      COALESCE(resultante.valororiginal, 0)               AS valororiginal,
      COALESCE(resultante.valoracumuladodaamortizacao, 0) AS valoracumuladodaamortizacao,
      COALESCE(resultante.valoracumuladodadepreciacao, 0) AS valoracumuladodadepreciacao,
      COALESCE(resultante.valoracumuladodaexaustao, 0)    AS valoracumuladodaexaustao,
      COALESCE(resultante.valoracumuladodeajuste, 0)      AS valoracumuladodeajuste,
      RESULTANTE.ID                                       AS ID_ULTIMO_ESTADO_BEM,
      EB.ID                                               AS ID_ULTIMO_EVENTO_BEM,
      B.DATAAQUISICAO
    FROM bem b
    INNER JOIN eventobem eb
    ON eb.bem_id = b.id
    INNER JOIN estadobem resultante
    ON RESULTANTE.ID      = EB.ESTADORESULTANTE_ID
    WHERE eb.dataoperacao =
      (SELECT MAX(eb2.dataoperacao) FROM eventobem eb2 WHERE eb2.bem_id = b.id
      )
    )