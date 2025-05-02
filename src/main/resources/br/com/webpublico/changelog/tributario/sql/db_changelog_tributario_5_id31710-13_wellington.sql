  CREATE OR REPLACE FORCE VIEW VWCONSULTADEDEBITOSSEMVALORES ("CADASTRO_ID", "PESSOA_ID", "DIVIDA_ID", "DIVIDA",
  "DIVIDA_ISDIVIDAATIVA", "CALCULO_ID", "VALORDIVIDA_ID", "PARCELA_ID", "SITUACAOPARCELA_ID", "SITUACAOPARCELA",
  "EXERCICIO_ID", "EXERCICIO", "OPCAOPAGAMENTO_ID", "VALORORIGINAL", "VALORCORRECAO", "VALORJUROS", "VALORMULTA",
  "VALORHONORARIOS", "VALORIMPOSTO", "VALORTAXA", "VALORDESCONTO", "PARCELA", "PROMOCIONAL", "VALORPAGO", "DATAPAGAMENTO",
  "EMISSAO", "PROCESSOCALCULO", "QUANTIDADEIMPRESSOESDAM", "VENCIMENTO", "CONFIGURACAOACRESCIMO_ID", "TIPOCALCULO",
  "DIVIDAATIVA", "DIVIDAATIVAAJUIZADA", "SD", "NUMEROPARCELA", "ORDEMAPRESENTACAO", "OBSERVACAO", "PERMISSAOEMISSAODAM",
  "ISENTA_ACRESCIMOS") AS
  SELECT DISTINCT CADASTRO.ID AS CADASTRO_ID,
        PESSOA.ID                 AS PESSOA_ID,
        DIVIDA.ID                 AS DIVIDA_ID,
        DIVIDA.DESCRICAO          AS DIVIDA,
        COALESCE(DIVIDA.ISDIVIDAATIVA, 0)      AS DIVIDA_ISDIVIDAATIVA,
        CALCULO.ID                AS CALCULO_ID,
        VD.ID                     AS VALORDIVIDA_ID,
        PVD.ID                    AS PARCELA_ID,
        SPVD.ID                   AS SITUACAOPARCELA_ID,
        SPVD.SITUACAOPARCELA      AS SITUACAOPARCELA,
        EXERCICIO.ID              AS EXERCICIO_ID,
        EXERCICIO.ANO             AS EXERCICIO,
        PVD.OPCAOPAGAMENTO_ID     AS OPCAOPAGAMENTO_ID,
        CASE
          WHEN SPVD.SITUACAOPARCELA <> 'EM_ABERTO'
          THEN PVD.VALOR
          ELSE SPVD.SALDO
        END                                                                                             AS VALORORIGINAL,
        0                                                                                               AS VALORCORRECAO,
        0                                                                                               AS VALORJUROS,
        0                                                                                               AS VALORMULTA,
        0                                                                                               AS VALORHONORARIOS,
        0                                                                                               AS VALORIMPOSTO,
        0                                                                                               AS VALORTAXA,
        0                                                                                               AS VALORDESCONTO,
        PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(VD.ID, OP.ID, OP.PROMOCIONAL, PVD.SEQUENCIAPARCELA) AS PARCELA,
        COALESCE(OP.PROMOCIONAL, 0)                                                                     AS PROMOCIONAL,
        0                                                                                               AS VALORPAGO,
        PACOTE_CONSULTA_DE_DEBITOS.GETDATAPAGAMENTOPARCELA(PVD.ID, SPVD.SITUACAOPARCELA)                AS DATAPAGAMENTO,
        VD.EMISSAO                                                                                      AS EMISSAO,
        SPVD.REFERENCIA                                                                                 AS PROCESSOCALCULO,
        PACOTE_CONSULTA_DE_DEBITOS.QUANTIDADEIMPRESSOESDAM(PVD.ID)                                      AS QUANTIDADEIMPRESSOESDAM,
        COALESCE(PVD.VENCIMENTO,SYSDATE)                                                                AS VENCIMENTO,
        DVA.ACRESCIMO_ID                                                                                AS CONFIGURACAOACRESCIMO_ID,
        CALCULO.TIPOCALCULO                                                                             AS TIPOCALCULO,
        COALESCE(PVD.DIVIDAATIVA,0)                                                                     AS DIVIDAATIVA,
        COALESCE(PVD.DIVIDAATIVAAJUIZADA,0)                                                             AS DIVIDAATIVAAJUIZADA,
        CALCULO.SUBDIVIDA                                                                               AS SD,
        REGEXP_REPLACE(PVD.SEQUENCIAPARCELA, '[^0-9]+', '')                                             AS NUMEROPARCELA,
        DIVIDA.ORDEMAPRESENTACAO                                                                        AS ORDEMAPRESENTACAO,
        calculo.observacao                                                                              AS OBSERVACAO,
        divida.permissaoEmissaoDAM                                                                      AS PERMISSAOEMISSAODAM,
        coalesce(calculo.isentaacrescimos, 0)                                                           AS ISENTA_ACRESCIMOS
       FROM PARCELAVALORDIVIDA PVD
      INNER JOIN OPCAOPAGAMENTO OP ON OP.ID = PVD.OPCAOPAGAMENTO_ID
      INNER JOIN VALORDIVIDA VD ON VD.ID = PVD.VALORDIVIDA_ID
      INNER JOIN CALCULO CALCULO ON CALCULO.ID = VD.CALCULO_ID
      INNER JOIN DIVIDA DIVIDA ON DIVIDA.ID = VD.DIVIDA_ID
      INNER JOIN EXERCICIO EXERCICIO ON EXERCICIO.ID = VD.EXERCICIO_ID
      INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD ON SPVD.ID = PVD.SITUACAOATUAL_ID
      LEFT JOIN CALCULOPESSOA CALCULOPESSOA ON CALCULOPESSOA.CALCULO_ID = CALCULO.ID
      LEFT JOIN PROCESSOCALCULO PROCESSO ON PROCESSO.ID = CALCULO.PROCESSOCALCULO_ID
      LEFT JOIN CADASTRO CADASTRO ON CADASTRO.ID = CALCULO.CADASTRO_ID
      LEFT JOIN PESSOA PESSOA ON PESSOA.ID = CALCULOPESSOA.PESSOA_ID
      LEFT JOIN DIVIDAACRESCIMO DVA ON DVA.DIVIDA_ID = DIVIDA.ID AND current_date BETWEEN DVA.INICIOVIGENCIA and COALESCE(DVA.FINALVIGENCIA, current_date)
