CREATE OR REPLACE VIEW VWCONSULTADEDEBITOSAPRESCREVER AS
SELECT DISTINCT CAD.ID                                                            ID_CADASTRO,
                CPES.PESSOA_ID                                                    ID_PESSOA,
                DIV.ID                                                            ID_DIVIDA,
                DIV.DESCRICAO                                                     DIVIDA,
                CALC.ID                                                           ID_CALCULO,
                CALC.TIPOCALCULO                                                  TIPOCALCULO,
                VD.ID                                                             VALORDIVIDA_ID,
                PVD.ID                                                            ID_PARCELA,
                PVD.DATAPRESCRICAO                                                DATAPRESCRICAO,
                PVD.OPCAOPAGAMENTO_ID                                             ID_OPCAOPAGAMENTO,
                OP.PROMOCIONAL                                                    COTAUNICA,
                SPVD.SITUACAOPARCELA                                              SITUACAOPARCELA,
                EX.ANO                                                            EXERCICIO,

                CASE
                    WHEN SPVD.SITUACAOPARCELA <> 'EM_ABERTO'
                        THEN PVD.VALOR
                    ELSE SPVD.SALDO
                    END                                                           VALORORIGINAL,

                COALESCE(VPVD.VALORIMPOSTO, -1)                                   VALORIMPOSTO,
                COALESCE(VPVD.VALORTAXA, -1)                                      VALORTAXA,
                COALESCE(VPVD.VALORJUROS, -1)                                     VALORJUROS,
                COALESCE(VPVD.VALORMULTA, -1)                                     VALORMULTA,
                COALESCE(VPVD.VALORCORRECAO, -1)                                  VALORCORRECAO,
                COALESCE(VPVD.VALORHONORARIOS, -1)                                VALORHONORARIOS,
                PACOTE_CONSULTA_DE_DEBITOS.GETNUMEROPARCELA(VD.ID, OP.ID, OP.PROMOCIONAL,
                                                            PVD.SEQUENCIAPARCELA) PARCELA,
                COALESCE(PVD.VENCIMENTO, CURRENT_DATE)                            VENCIMENTO,
                COALESCE(PF.CPF, PJ.CNPJ)                                         CPFORCNPJ,
                COALESCE(PF.NOME, PJ.RAZAOSOCIAL)                                 NOMEORRAZAOSOCIAL,
                DVA.ACRESCIMO_ID                                                  ID_CONFIGURACAOACRESCIMO,
                SPVD.REFERENCIA,
                EX.ID                                                             ID_EXERCICIO

FROM PARCELAVALORDIVIDA PVD
         INNER JOIN OPCAOPAGAMENTO OP ON OP.ID = PVD.OPCAOPAGAMENTO_ID
         INNER JOIN VALORDIVIDA VD ON VD.ID = PVD.VALORDIVIDA_ID
         INNER JOIN CALCULO CALC ON CALC.ID = VD.CALCULO_ID
         INNER JOIN CADASTRO CAD ON CAD.ID = CALC.CADASTRO_ID
         INNER JOIN DIVIDA DIV ON DIV.ID = VD.DIVIDA_ID
         INNER JOIN EXERCICIO EX ON EX.ID = VD.EXERCICIO_ID
         INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD ON SPVD.ID = PVD.SITUACAOATUAL_ID
         LEFT JOIN CALCULOPESSOA CPES ON CPES.CALCULO_ID = CALC.ID
         LEFT JOIN PESSOA PES ON PES.ID = CPES.PESSOA_ID
         LEFT JOIN PESSOAJURIDICA PJ ON PJ.ID = CPES.PESSOA_ID
         LEFT JOIN PESSOAFISICA PF ON PF.ID = CPES.PESSOA_ID
         LEFT JOIN DIVIDAACRESCIMO DVA ON DVA.DIVIDA_ID = DIV.ID AND
                                          CURRENT_DATE BETWEEN DVA.INICIOVIGENCIA AND COALESCE(DVA.FINALVIGENCIA, CURRENT_DATE)
         LEFT JOIN VALORESPARCELAVALORDIVIDA VPVD ON VPVD.PARCELA_ID = PVD.ID
