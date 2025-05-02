update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638887558
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));



update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638982954
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('30/01/2020', 'dd/MM/yyyy'));



update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639026769
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy'));
