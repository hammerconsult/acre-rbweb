  CREATE OR REPLACE FORCE VIEW VWBEM (BEM_ID, IDENTIFICACAO_PATRIMONIAL, DESCRICAO, VALORORIGINAL, VALORACUMULADODAAMORTIZACAO, VALORACUMULADODADEPRECIACAO, VALORACUMULADODAEXAUSTAO, VALORACUMULADODEAJUSTE, ID_ULTIMO_ESTADO_BEM, ID_ULTIMO_EVENTO_BEM)
  AS
    (SELECT b.id      AS bem_id,
      b.identificacao AS identificacao_patrimonial,
      b.descricao,
      COALESCE(resultante.valororiginal, 0)               AS valororiginal,
      COALESCE(resultante.valoracumuladodaamortizacao, 0) AS valoracumuladodaamortizacao,
      COALESCE(resultante.valoracumuladodadepreciacao, 0) AS valoracumuladodadepreciacao,
      COALESCE(resultante.valoracumuladodaexaustao, 0)    AS valoracumuladodaexaustao,
      COALESCE(resultante.valoracumuladodeajuste, 0)      AS valoracumuladodeajuste,
      resultante.id                                       AS id_ultimo_estado_bem,
      eb.id                                               AS id_ultimo_evento_bem
    FROM bem b
    INNER JOIN eventobem eb
    ON eb.bem_id = b.id
    INNER JOIN estadobem resultante
    ON RESULTANTE.ID = EB.ESTADORESULTANTE_ID
    WHERE eb.dataoperacao      =
      (SELECT MAX(eb2.dataoperacao) FROM eventobem eb2 WHERE eb2.bem_id = b.id
      )
    )