-- 544781 - HELLYETH SILVA DUARTE id - 638982954

insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('05/04/2021', 'dd/MM/yyyy'), to_date('03/07/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 638982954
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('30/01/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638982954
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('30/01/2020', 'dd/MM/yyyy')));
