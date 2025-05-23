create or replace view VWCONSULTADEDEBITOS as
    SELECT DISTINCT CADASTRO.ID                                                                                  AS CADASTRO_ID,
                    PESSOA.ID                                                                                    AS PESSOA_ID,
                    DIVIDA.ID                                                                                    AS DIVIDA_ID,
                    DIVIDA.DESCRICAO                                                                             AS DIVIDA,
                    COALESCE(DIVIDA.ISDIVIDAATIVA, 0)                                                            AS DIVIDA_ISDIVIDAATIVA,
                    CALCULO.ID                                                                                   AS CALCULO_ID,
                    VD.ID                                                                                        AS VALORDIVIDA_ID,
                    PVD.ID                                                                                       AS PARCELA_ID,
                    SPVD.ID                                                                                      AS SITUACAOPARCELA_ID,
                    SPVD.SITUACAOPARCELA                                                                         AS SITUACAOPARCELA,
                    EXERCICIO.ID                                                                                 AS EXERCICIO_ID,
                    EXERCICIO.ANO                                                                                AS EXERCICIO,
                    PVD.OPCAOPAGAMENTO_ID                                                                        AS OPCAOPAGAMENTO_ID,
                    CASE
                        WHEN SPVD.SITUACAOPARCELA <> 'EM_ABERTO'
                              THEN PVD.VALOR
                        ELSE SPVD.SALDO
                        END                                                                  AS VALORORIGINAL,
                    COALESCE(vpvd.VALORCORRECAO, -1)                                                             AS VALORCORRECAO,
                    COALESCE(vpvd.VALORJUROS, -1)                                                                AS VALORJUROS,
                    COALESCE(vpvd.VALORMULTA, -1)                                                                AS VALORMULTA,
                    COALESCE(vpvd.VALORHONORARIOS, -1)                                                           AS VALORHONORARIOS,
                    COALESCE(vpvd.valorimposto, -1)                                                              AS VALORIMPOSTO,
                    COALESCE(vpvd.VALORTAXA, -1)                                                                 AS VALORTAXA,
                    0                                                                                            AS VALORDESCONTO,
                    PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(VD.ID, OP.ID, OP.PROMOCIONAL,
                                                                PVD.SEQUENCIAPARCELA)        AS PARCELA,
                    COALESCE(OP.PROMOCIONAL, 0)                                                                  AS PROMOCIONAL,
                    COALESCE(vpvd.VALORPAGO, -1)                                                                 AS VALORPAGO,
                    PACOTE_CONSULTA_DE_DEBITOS.GETDATAPAGAMENTOPARCELA(PVD.ID,
                                                                       SPVD.SITUACAOPARCELA) AS DATAPAGAMENTO,
                    VD.EMISSAO                                                                                   AS EMISSAO,
                    SPVD.REFERENCIA                                                                              AS PROCESSOCALCULO,
                    PACOTE_CONSULTA_DE_DEBITOS.QUANTIDADEIMPRESSOESDAM(PVD.ID)                                   AS QUANTIDADEIMPRESSOESDAM,
                    COALESCE(PVD.VENCIMENTO, SYSDATE)                                                            AS VENCIMENTO,
                    DVA.ACRESCIMO_ID                                                                             AS CONFIGURACAOACRESCIMO_ID,
                    CALCULO.TIPOCALCULO                                                                          AS TIPOCALCULO,
                    COALESCE(PVD.DIVIDAATIVA, 0)                                                                 AS DIVIDAATIVA,
                    COALESCE(PVD.DIVIDAATIVAAJUIZADA, 0)                                                         AS DIVIDAATIVAAJUIZADA,
                    CALCULO.SUBDIVIDA                                                                            AS SD,
                    REGEXP_REPLACE(PVD.SEQUENCIAPARCELA, '[^0-9]+', '')                                          AS NUMEROPARCELA,
                    DIVIDA.ORDEMAPRESENTACAO                                                                     AS ORDEMAPRESENTACAO,
                    calculo.observacao                                                                           AS OBSERVACAO,
                    divida.permissaoEmissaoDAM                                                                   AS PERMISSAOEMISSAODAM,
                    vpvd.ULTIMAATUALIZACAO                                                                       AS ULTIMAATUALIZACAOVALORES

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
             LEFT JOIN DIVIDAACRESCIMO DVA ON DVA.DIVIDA_ID = DIVIDA.ID AND
                                              current_date BETWEEN DVA.INICIOVIGENCIA and COALESCE(DVA.FINALVIGENCIA, current_date)
             left join VALORESPARCELAVALORDIVIDA vpvd on vpvd.PARCELA_ID = pvd.id
