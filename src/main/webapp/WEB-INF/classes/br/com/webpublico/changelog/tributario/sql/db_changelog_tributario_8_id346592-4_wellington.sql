CREATE OR REPLACE FORCE EDITIONABLE VIEW "VWCONSULTADEDEBITOSSEMVALORES" ("CADASTRO_ID", "PESSOA_ID",
       "DIVIDA_ID", "DIVIDA", "DIVIDA_ISDIVIDAATIVA", "CALCULO_ID", "VALORDIVIDA_ID", "PARCELA_ID",
       "SITUACAOPARCELA_ID", "SITUACAOPARCELA", "EXERCICIO_ID", "EXERCICIO", "OPCAOPAGAMENTO_ID",
       "VALORORIGINAL", "VALORCORRECAO", "VALORJUROS", "VALORMULTA", "VALORHONORARIOS", "VALORIMPOSTO",
       "VALORTAXA", "VALORDESCONTO", "PARCELA", "PROMOCIONAL", "VALORPAGO", "DATAPAGAMENTO", "EMISSAO",
       "PROCESSOCALCULO", "QUANTIDADEIMPRESSOESDAM", "VENCIMENTO", "CONFIGURACAOACRESCIMO_ID",
       "TIPOCALCULO", "DIVIDAATIVA", "DIVIDAATIVAAJUIZADA", "SD", "NUMEROPARCELA", "ORDEMAPRESENTACAO",
       "OBSERVACAO", "PERMISSAOEMISSAODAM", "ISENTA_ACRESCIMOS", "DEBITOPROTESTADO", "DATAPROTESTO") AS
select distinct cadastro.id                                                                                     as cadastro_id,
                pessoa.id                                                                                       as pessoa_id,
                divida.id                                                                                       as divida_id,
                divida.descricao                                                                                as divida,
                coalesce(divida.isdividaativa, 0)                                                               as divida_isdividaativa,
                calculo.id                                                                                      as calculo_id,
                vd.id                                                                                           as valordivida_id,
                pvd.id                                                                                          as parcela_id,
                spvd.id                                                                                         as situacaoparcela_id,
                spvd.situacaoparcela                                                                            as situacaoparcela,
                exercicio.id                                                                                    as exercicio_id,
                exercicio.ano                                                                                   as exercicio,
                pvd.opcaopagamento_id                                                                           as opcaopagamento_id,
                case
                    when spvd.situacaoparcela <> 'EM_ABERTO'
                        then pvd.valor
                    else spvd.saldo
                    end                                                                                         as valororiginal,
                0                                                                                               as valorcorrecao,
                0                                                                                               as valorjuros,
                0                                                                                               as valormulta,
                0                                                                                               as valorhonorarios,
                0                                                                                               as valorimposto,
                0                                                                                               as valortaxa,
                0                                                                                               as valordesconto,
                pacote_consulta_de_debitos.getnumeroparcela(vd.id, op.id, op.promocional,
                                                            pvd.sequenciaparcela)                               as parcela,
                coalesce(op.promocional, 0)                                                                     as promocional,
                0                                                                                               as valorpago,
                pacote_consulta_de_debitos.getdatapagamentoparcela(pvd.id,
                                                                   spvd.situacaoparcela)                        as datapagamento,
                vd.emissao                                                                                      as emissao,
                spvd.referencia                                                                                 as processocalculo,
                pacote_consulta_de_debitos.quantidadeimpressoesdam(pvd.id)                                      as quantidadeimpressoesdam,
                coalesce(pvd.vencimento, sysdate)                                                               as vencimento,
                dva.acrescimo_id                                                                                as configuracaoacrescimo_id,
                calculo.tipocalculo                                                                             as tipocalculo,
                coalesce(pvd.dividaativa, 0)                                                                    as dividaativa,
                coalesce(pvd.dividaativaajuizada, 0)                                                            as dividaativaajuizada,
                calculo.subdivida                                                                               as sd,
                regexp_replace(pvd.sequenciaparcela, '[^0-9]+', '')                                             as numeroparcela,
                divida.ordemapresentacao                                                                        as ordemapresentacao,
                calculo.observacao                                                                              as observacao,
                divida.permissaoemissaodam                                                                      as permissaoemissaodam,
                coalesce(calculo.isentaacrescimos, 0)                                                           as isenta_acrescimos,
                coalesce(pvd.debitoprotestado, 0)                                                               as debitoprotestado,
                pvd.dataprotesto                                                                                as dataprotesto
from parcelavalordivida pvd
         inner join opcaopagamento op on op.id = pvd.opcaopagamento_id
         inner join valordivida vd on vd.id = pvd.valordivida_id
         inner join calculo on calculo.id = vd.calculo_id
         inner join divida on divida.id = vd.divida_id
         inner join exercicio on exercicio.id = vd.exercicio_id
         inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
         left join calculopessoa on calculopessoa.calculo_id = calculo.id
         left join processocalculo processo on processo.id = calculo.processocalculo_id
         left join cadastro on cadastro.id = calculo.cadastro_id
         left join pessoa on pessoa.id = calculopessoa.pessoa_id
         left join dividaacrescimo dva on dva.divida_id = divida.id and
                                          current_date between dva.iniciovigencia and coalesce(dva.finalvigencia, current_date)
