    CREATE OR REPLACE FORCE VIEW VWCONSULTARENDAS
    AS
      (SELECT CONTRATO.ID ID_CONTRATO,
        CONTRATO.NUMEROCONTRATO NUMEROCONTRATO,
       (SELECT LOC.DESCRICAO
                   FROM PTOCOMERCIALCONTRATORENDAS REL
                  INNER JOIN PONTOCOMERCIAL PONTO ON REL.PONTOCOMERCIAL_ID = PONTO.ID
                  INNER JOIN LOCALIZACAO LOC ON PONTO.LOCALIZACAO_ID = LOC.ID
                WHERE REL.CONTRATORENDASPATRIMONIAIS_ID = CONTRATO.ID
                  AND ROWNUM = 1) LOCALIZACAO,
       (SELECT LISTAGG(PONTO.NUMEROBOX, '; ') WITHIN GROUP (ORDER BY PONTO.NUMEROBOX)
                   FROM PTOCOMERCIALCONTRATORENDAS REL
                  INNER JOIN PONTOCOMERCIAL PONTO ON REL.PONTOCOMERCIAL_ID = PONTO.ID
                WHERE REL.CONTRATORENDASPATRIMONIAIS_ID = CONTRATO.ID) BOX,
        E.ANO EXERCICIO,
        CP.PESSOA_ID ID_PESSOA,
        COALESCE(PF.CPF, PJ.CNPJ) CPFCNPJ,
        COALESCE(PF.NOME, PJ.RAZAOSOCIAL) NOMERAZAO,
        C.DATACALCULO DATALANCAMENTO,
        PVD.ID ID_PARCELA,
        PVD.OPCAOPAGAMENTO_ID ID_OPCAOPAGAMENTO,
        C.SUBDIVIDA,
        PVD.VENCIMENTO,
        SPVD.SITUACAOPARCELA,
        SPVD.REFERENCIA,
        PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(VD.ID, OP.ID, OP.PROMOCIONAL, PVD.SEQUENCIAPARCELA) PARCELA,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORIMPOSTO(PVD.ID,1, SYSDATE), 2), 0)                                      AS VALORIMPOSTO,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORTAXA(PVD.ID,1, SYSDATE), 2), 0)                                         AS VALORTAXA,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORCORRECAO( PVD.ID, EXTRACT(YEAR FROM PVD.VENCIMENTO),1, SYSDATE), 2), 0) AS VALORCORRECAO,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORJUROS(SYSDATE, PVD.ID,1), 2), 0)                                        AS VALORJUROS,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORMULTA(SYSDATE, PVD.ID,1), 2), 0)                                        AS VALORMULTA,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORHONORARIOS(PVD.ID, SYSDATE), 2), 0)                                     AS VALORHONORARIOS,
        COALESCE(ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORDESCONTO(PVD.ID, SYSDATE), 2), 0)                                       AS VALORDESCONTO,
        COALESCE(PACOTE_CONSULTA_DE_DEBITOS.GETVALORPARCELAPAGO(PVD.ID, SPVD.SITUACAOPARCELA), 0)                                 AS VALORPAGO
       FROM CALCULORENDAS RENDAS
      INNER JOIN CONTRATORENDASPATRIMONIAIS CONTRATO ON CONTRATO.ID = RENDAS.CONTRATO_ID
      INNER JOIN CALCULO C ON C.ID = RENDAS.ID
      INNER JOIN CALCULOPESSOA CP  ON CP.CALCULO_ID = RENDAS.ID
      LEFT JOIN PESSOAJURIDICA PJ  ON PJ.ID = CP.PESSOA_ID
      LEFT JOIN PESSOAFISICA PF  ON PF.ID = CP.PESSOA_ID
      INNER JOIN VALORDIVIDA VD  ON VD.CALCULO_ID = RENDAS.ID
      INNER JOIN EXERCICIO E ON E.ID = VD.EXERCICIO_ID
      INNER JOIN PARCELAVALORDIVIDA PVD  ON PVD.VALORDIVIDA_ID = VD.ID
      INNER JOIN OPCAOPAGAMENTO OP  ON OP.ID = PVD.OPCAOPAGAMENTO_ID
      INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD  ON SPVD.ID = GETULTIMASITUACAO(PVD.ID)
     WHERE SPVD.SITUACAOPARCELA <> 'ISOLAMENTO')

                
                
                
                