
  CREATE OR REPLACE FORCE VIEW VWCONSULTADEDEBITOS
  AS
  SELECT DISTINCT CADASTRO.ID AS CADASTRO_ID,
    PESSOA.ID                 AS PESSOA_ID,
    DIVIDA.ID                 AS DIVIDA_ID,
    CALCULO.ID                AS CALCULO_ID,
    VD.ID                     AS VALORDIVIDA_ID,
    PVD.ID                    AS PARCELA_ID,
    SPVD.ID                   AS SITUACAOPARCELA_ID,
    SPVD.SITUACAOPARCELA      AS SITUACAOPARCELA,
    EXERCICIO.ID              AS EXERCICIO_ID,
    PVD.OPCAOPAGAMENTO_ID     AS OPCAOPAGAMENTO_ID,
    CASE
      WHEN SPVD.SITUACAOPARCELA <> 'EM_ABERTO'
      THEN PVD.VALOR
      ELSE SPVD.SALDO
    END                                                                                                          AS VALORORIGINAL,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORCORRECAO( PVD.ID, EXTRACT(YEAR FROM pvd.vencimento),1, SYSDATE), 2) AS VALORCORRECAO,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORJUROS(SYSDATE, PVD.ID,1), 2)                                        AS VALORJUROS,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORMULTA(SYSDATE, PVD.ID,1), 2)                                        AS VALORMULTA,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORHONORARIOS(PVD.ID, SYSDATE), 2)                                     AS VALORHONORARIOS,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORIMPOSTO(PVD.ID,1, SYSDATE), 2)                                      AS VALORIMPOSTO,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORTAXA(PVD.ID,1, SYSDATE), 2)                                         AS VALORTAXA,
    ROUND(PACOTE_CONSULTA_DE_DEBITOS.GETVALORDESCONTO(PVD.ID, SYSDATE), 2)                                       AS VALORDESCONTO,
    PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(VD.ID, OP.ID, OP.PROMOCIONAL, PVD.SEQUENCIAPARCELA)              AS PARCELA,
    COALESCE(OP.PROMOCIONAL, 0)                                                                                  AS PROMOCIONAL,
    PACOTE_CONSULTA_DE_DEBITOS.GETVALORPARCELAPAGO(PVD.ID, SPVD.SITUACAOPARCELA)                                 AS VALORPAGO,
    PACOTE_CONSULTA_DE_DEBITOS.GETDATAPAGAMENTOPARCELA(PVD.ID, SPVD.SITUACAOPARCELA)                             AS DATAPAGAMENTO,
    VD.EMISSAO                                                                                                   AS EMISSAO,
    SPVD.REFERENCIA                                                                                              AS PROCESSOCALCULO,
    PACOTE_CONSULTA_DE_DEBITOS.QUANTIDADEIMPRESSOESDAM(PVD.ID)                                                   AS QUANTIDADEIMPRESSOESDAM,
    PVD.VENCIMENTO                                                                                               AS VENCIMENTO,
    DVA.ACRESCIMO_ID                                                                                             AS CONFIGURACAOACRESCIMO_ID,
    CALCULO.TIPOCALCULO                                                                                          AS TIPOCALCULO,
    COALESCE(pvd.dividaativa,0)                                                                                  AS DIVIDAATIVA,
    COALESCE(pvd.dividaativaajuizada,0)                                                                          AS DIVIDAATIVAAJUIZADA,
    DIVIDA.DESCRICAO                                                                                             AS DIVIDA,
    EXERCICIO.ANO                                                                                                AS EXERCICIO,
    CALCULO.SUBDIVIDA                                                                                            AS SD,
    CASE WHEN CALCULO.TIPOCALCULO = 'NFSE' AND
              to_date(PVD.VENCIMENTO , 'dd/MM/yyyy') < to_date(SYSDATE, 'dd/MM/yyyy') THEN 1
         ELSE COALESCE(DIVIDA.ISDIVIDAATIVA,0) END                                                               AS BLOQUEIAIMPRESSAO,
    CALCULO.OBSERVACAO                                                                                           AS OBSERVACAO,
    REGEXP_REPLACE(PVD.SEQUENCIAPARCELA, '[^0-9]+', '')                                                         AS NUMEROPARCELA
  FROM PARCELAVALORDIVIDA PVD
  INNER JOIN OPCAOPAGAMENTO OP
  ON OP.ID = PVD.OPCAOPAGAMENTO_ID
  INNER JOIN VALORDIVIDA VD
  ON VD.ID = PVD.VALORDIVIDA_ID
  INNER JOIN CALCULO CALCULO
  ON CALCULO.ID = VD.CALCULO_ID
  INNER JOIN DIVIDA DIVIDA
  ON DIVIDA.ID = VD.DIVIDA_ID
  INNER JOIN EXERCICIO EXERCICIO
  ON EXERCICIO.ID = VD.EXERCICIO_ID
  INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD
  ON SPVD.ID = PACOTE_CONSULTA_DE_DEBITOS.GETULTIMASITUACAOPARCELA(PVD.ID)
  LEFT JOIN CALCULOPESSOA CALCULOPESSOA
  ON CALCULOPESSOA.CALCULO_ID = CALCULO.ID
  LEFT JOIN PROCESSOCALCULO PROCESSO
  ON PROCESSO.ID =CALCULO.PROCESSOCALCULO_ID
  LEFT JOIN CADASTRO CADASTRO
  ON CADASTRO.ID = CALCULO.CADASTRO_ID
  LEFT JOIN pessoa pessoa
  ON PESSOA.ID = CALCULOPESSOA.PESSOA_ID
  INNER JOIN DividaAcrescimo DVA
  ON DVA.DIVIDA_ID = DIVIDA.ID
