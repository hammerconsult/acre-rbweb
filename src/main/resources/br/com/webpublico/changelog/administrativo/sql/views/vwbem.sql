CREATE OR REPLACE FORCE VIEW VWBEM
AS
  (SELECT DISTINCT b.id      AS bem_id,
    b.identificacao AS identificacao_patrimonial,
    b.descricao,
    COALESCE(resultante.valororiginal, 0)               AS valororiginal,
    COALESCE(resultante.valoracumuladodaamortizacao, 0) AS valoracumuladodaamortizacao,
    COALESCE(resultante.valoracumuladodadepreciacao, 0) AS valoracumuladodadepreciacao,
    COALESCE(resultante.valoracumuladodaexaustao, 0)    AS valoracumuladodaexaustao,
    COALESCE(resultante.valoracumuladodeajuste, 0)      AS valoracumuladodeajuste,
    RESULTANTE.ID                                       AS ID_ULTIMO_ESTADO_BEM,
    EB.ID                                               AS ID_ULTIMO_EVENTO_BEM,
    B.DATAAQUISICAO,
    RESULTANTE.ID,
    VW.CODIGO AS CODIGO_ADMINISTRATIVA,
    VW.DESCRICAO AS DESCRICAO_ADMINISTRATIVA,
    VWORC.CODIGO AS CODIGO_ORCAMENTARIA,
    VWORC.DESCRICAO AS DESCRICAO_ORCAMENTARIA    
  FROM bem b
  INNER JOIN EVENTOBEM EB  ON EB.BEM_ID = B.ID
  LEFT JOIN ESTADOBEM INICIAL  ON INICIAL.ID   = EB.ESTADOINICIAL_ID
  INNER JOIN ESTADOBEM RESULTANTE  ON RESULTANTE.ID   = EB.ESTADORESULTANTE_ID
  INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = CASE WHEN EB.TIPOEVENTOBEM = 'EFETIVACAOCESSAO' THEN INICIAL.DETENTORAADMINISTRATIVA_ID
                                                                       ELSE RESULTANTE.DETENTORAADMINISTRATIVA_ID
                                                                  END
  INNER JOIN VWHIERARQUIAORCAMENTARIA VWORC ON VWORC.SUBORDINADA_ID = CASE WHEN EB.TIPOEVENTOBEM = 'EFETIVACAOCESSAO' THEN INICIAL.DETENTORAORCAMENTARIA_ID
                                                                       ELSE RESULTANTE.DETENTORAORCAMENTARIA_ID
                                                                  END
  WHERE eb.dataoperacao =
    (SELECT MAX(eb2.dataoperacao) FROM eventobem eb2 WHERE eb2.bem_id = b.id
    )
    AND SYSDATE BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, SYSDATE)
    AND SYSDATE BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, SYSDATE)
  )