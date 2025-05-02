insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2021', 'dd/MM/yyyy'), to_date('29/06/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639026769
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639026769
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')));
