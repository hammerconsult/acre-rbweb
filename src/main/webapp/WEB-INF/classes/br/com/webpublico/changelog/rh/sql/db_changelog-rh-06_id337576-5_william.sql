update CONCESSAOFERIASLICENCA set DIAS = 90 where id in (select concessao.id from concessaoferiaslicenca concessao
                                                                                                             inner join PERIODOAQUISITIVOFL pa on concessao.PERIODOAQUISITIVOFL_ID = pa.ID
                                                                                                             inner join basecargo on pa.BASECARGO_ID = BASECARGO.ID
                                                                                                             inner join BasePeriodoAquisitivo basepa on BASECARGO.BASEPERIODOAQUISITIVO_ID = basepa.ID
                                                         where dias is null);



update PERIODOAQUISITIVOFL set STATUS = 'CONCEDIDO' where id in (select pa.id from concessaoferiaslicenca concessao
                                                                                              inner join PERIODOAQUISITIVOFL pa on concessao.PERIODOAQUISITIVOFL_ID = pa.ID
                                                                                              inner join basecargo on pa.BASECARGO_ID = BASECARGO.ID
                                                                                              inner join BasePeriodoAquisitivo basepa on BASECARGO.BASEPERIODOAQUISITIVO_ID = basepa.ID
                                                                 where dias is null);
