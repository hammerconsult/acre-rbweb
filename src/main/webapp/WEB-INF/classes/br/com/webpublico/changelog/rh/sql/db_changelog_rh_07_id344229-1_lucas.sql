update PERIODOAQUISITIVOFL periodo
set periodo.SALDO  = periodo.SALDODEDIREITO,
    periodo.STATUS = 'NAO_CONCEDIDO'
where periodo.id in (select pa.id
                     from PERIODOAQUISITIVOFL pa
                              inner join BASECARGO base on pa.BASECARGO_ID = base.ID
                              inner join BASEPERIODOAQUISITIVO basePeriodo
                                         on base.BASEPERIODOAQUISITIVO_ID = basePeriodo.ID
                              inner join vinculofp vinc on pa.CONTRATOFP_ID = vinc.id
                     where pa.SALDO < pa.SALDODEDIREITO
                       and basePeriodo.TIPOPERIODOAQUISITIVO = 'FERIAS'
                       and current_date between vinc.INICIOVIGENCIA and coalesce(vinc.FINALVIGENCIA, current_date)
                       and pa.id not in (select conc.PERIODOAQUISITIVOFL_ID
                                         from CONCESSAOFERIASLICENCA conc
                                         where conc.PERIODOAQUISITIVOFL_ID = pa.id));
