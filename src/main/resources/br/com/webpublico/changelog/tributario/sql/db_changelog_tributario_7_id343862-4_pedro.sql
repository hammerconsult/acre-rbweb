insert into PROPRIEDADEORIGINALITBI (ID, PROPRIEDADE_ID, PROPRIEDADERURAL_ID, PROCESSOCALCULOITBI_ID)
select HIBERNATE_SEQUENCE.nextval,
       P.ID,
       NULL,
       PCITBI.ID
from PROPRIEDADE p
         inner join PROCESSOCALCULOITBI PCITBI ON PCITBI.CADASTROIMOBILIARIO_ID = P.IMOVEL_ID
WHERE p.PESSOA_ID in (select tci.PESSOA_ID
                      from TRANSMITENTESCALCULOITBI tci
                      where tci.CALCULOITBI_ID = (select ci.id
                                                  from CALCULOITBI ci
                                                  where ci.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                                    and coalesce(ci.ORDEMCALCULO, 1) = 1))
  and TRUNC(P.FINALVIGENCIA) between (SELECT MAX(TRUNC(COALESCE(ilb.DATAPAGAMENTO, PIX.DATAPAGAMENTO)))
                                      FROM dam d
                                               INNER JOIN itemdam idam ON idam.dam_id = d.id
                                               LEFT JOIN itemlotebaixa ilb ON ilb.dam_id = d.id
                                               LEFT JOIN PIX ON d.PIX_ID = PIX.ID
                                               inner join parcelavalordivida pvd on idam.PARCELA_ID = pvd.ID
                                               inner join SITUACAOPARCELAVALORDIVIDA spvd on pvd.SITUACAOATUAL_ID = spvd.ID
                                               inner join valordivida vd on vd.id = pvd.valordivida_id
                                               inner join calculoitbi citbi on vd.CALCULO_ID = citbi.ID
                                      where spvd.SITUACAOPARCELA = 'PAGO'
                                        and citbi.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                        and coalesce(citbi.ORDEMCALCULO, 1) = (select max(coalesce(cit.ORDEMCALCULO, 1))
                                                                               from CALCULOITBI cit
                                                                               where cit.PROCESSOCALCULOITBI_ID = PCITBI.ID)) and (SELECT MAX(TRUNC(COALESCE(ilb.DATAPAGAMENTO, PIX.DATAPAGAMENTO))) + 3
                                                                                                                                   FROM dam d
                                                                                                                                            INNER JOIN itemdam idam ON idam.dam_id = d.id
                                                                                                                                            LEFT JOIN PIX ON d.PIX_ID = PIX.ID
                                                                                                                                            LEFT JOIN itemlotebaixa ilb ON ilb.dam_id = d.id
                                                                                                                                            inner join parcelavalordivida pvd on idam.PARCELA_ID = pvd.ID
                                                                                                                                            inner join SITUACAOPARCELAVALORDIVIDA spvd on pvd.SITUACAOATUAL_ID = spvd.ID
                                                                                                                                            inner join valordivida vd on vd.id = pvd.valordivida_id
                                                                                                                                            inner join calculoitbi citbi on vd.CALCULO_ID = citbi.ID
                                                                                                                                   where spvd.SITUACAOPARCELA = 'PAGO'
                                                                                                                                     and citbi.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                                                                                                                     and coalesce(citbi.ORDEMCALCULO, 1) =
                                                                                                                                         (select max(coalesce(cit.ORDEMCALCULO, 1))
                                                                                                                                          from CALCULOITBI cit
                                                                                                                                          where cit.PROCESSOCALCULOITBI_ID = PCITBI.ID));

insert into PROPRIEDADEORIGINALITBI (ID, PROPRIEDADE_ID, PROPRIEDADERURAL_ID, PROCESSOCALCULOITBI_ID)
select HIBERNATE_SEQUENCE.nextval,
       NULL,
       P.ID,
       PCITBI.ID
from PROPRIEDADERURAL p
         inner join PROCESSOCALCULOITBI PCITBI ON PCITBI.CADASTRORURAL_ID = P.IMOVEL_ID
WHERE p.PESSOA_ID in (select tci.PESSOA_ID
                      from TRANSMITENTESCALCULOITBI tci
                      where tci.CALCULOITBI_ID = (select ci.id
                                                  from CALCULOITBI ci
                                                  where ci.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                                    and coalesce(ci.ORDEMCALCULO, 1) = 1))
  and TRUNC(P.FINALVIGENCIA) between (SELECT MAX(TRUNC(COALESCE(ilb.DATAPAGAMENTO, PIX.DATAPAGAMENTO)))
                                      FROM dam d
                                               INNER JOIN itemdam idam ON idam.dam_id = d.id
                                               LEFT JOIN PIX ON d.PIX_ID = PIX.ID
                                               LEFT JOIN itemlotebaixa ilb ON ilb.dam_id = d.id
                                               inner join parcelavalordivida pvd on idam.PARCELA_ID = pvd.ID
                                               inner join SITUACAOPARCELAVALORDIVIDA spvd on pvd.SITUACAOATUAL_ID = spvd.ID
                                               inner join valordivida vd on vd.id = pvd.valordivida_id
                                               inner join calculoitbi citbi on vd.CALCULO_ID = citbi.ID
                                      where spvd.SITUACAOPARCELA = 'PAGO'
                                        and citbi.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                        and coalesce(citbi.ORDEMCALCULO, 1) = (select max(coalesce(cit.ORDEMCALCULO, 1))
                                                                               from CALCULOITBI cit
                                                                               where cit.PROCESSOCALCULOITBI_ID = PCITBI.ID)) and (SELECT MAX(TRUNC(COALESCE(ilb.DATAPAGAMENTO, PIX.DATAPAGAMENTO))) + 3
                                                                                                                                   FROM dam d
                                                                                                                                            INNER JOIN itemdam idam ON idam.dam_id = d.id
                                                                                                                                            LEFT JOIN PIX ON d.PIX_ID = PIX.ID
                                                                                                                                            LEFT JOIN itemlotebaixa ilb ON ilb.dam_id = d.id
                                                                                                                                            inner join parcelavalordivida pvd on idam.PARCELA_ID = pvd.ID
                                                                                                                                            inner join SITUACAOPARCELAVALORDIVIDA spvd on pvd.SITUACAOATUAL_ID = spvd.ID
                                                                                                                                            inner join valordivida vd on vd.id = pvd.valordivida_id
                                                                                                                                            inner join calculoitbi citbi on vd.CALCULO_ID = citbi.ID
                                                                                                                                   where spvd.SITUACAOPARCELA = 'PAGO'
                                                                                                                                     and citbi.PROCESSOCALCULOITBI_ID = PCITBI.ID
                                                                                                                                     and coalesce(citbi.ORDEMCALCULO, 1) =
                                                                                                                                         (select max(coalesce(cit.ORDEMCALCULO, 1))
                                                                                                                                          from CALCULOITBI cit
                                                                                                                                          where cit.PROCESSOCALCULOITBI_ID = PCITBI.ID));
