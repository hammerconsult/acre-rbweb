update PERIODOAQUISITIVOFL
set SALDO = 0,
    STATUS = 'CONCEDIDO'
where id in (select distinct pa.ID
             from  periodoAquisitivoFl pa
                       inner join CONCESSAOFERIASLICENCA ferias on pa.ID = ferias.PERIODOAQUISITIVOFL_ID
                       inner join BASECARGO base on pa.BASECARGO_ID = base.ID
                       inner join BASEPERIODOAQUISITIVO basePeriodo on base.BASEPERIODOAQUISITIVO_ID = basePeriodo.ID
             where pa.SALDO > 0
               and pa.SALDODEDIREITO > 0
               and (select sum(conc.DIAS)
                                        from CONCESSAOFERIASLICENCA conc
                                        where conc.PERIODOAQUISITIVOFL_ID = pa.id) >= pa.SALDODEDIREITO
               and basePeriodo.TIPOPERIODOAQUISITIVO = 'FERIAS');
