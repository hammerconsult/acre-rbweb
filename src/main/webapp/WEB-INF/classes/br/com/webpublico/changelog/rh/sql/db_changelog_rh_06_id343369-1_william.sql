insert into LancamentoTercoFeriasAut
select HIBERNATE_SEQUENCE.nextval, periodo.id, periodo.saldo, extract (month from  periodo.FINALVIGENCIA),extract (year from  periodo.FINALVIGENCIA)
from PERIODOAQUISITIVOFL periodo
         inner join basecargo on periodo.BASECARGO_ID = BASECARGO.ID
         inner join BasePeriodoAquisitivo basepa on BASECARGO.BASEPERIODOAQUISITIVO_ID = basepa.ID
         inner join contratofp on periodo.CONTRATOFP_ID = contratofp.ID
         inner join vinculofp vinculo on contratofp.id = vinculo.id
         inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.ID
         inner join pessoafisica pf on mat.PESSOA_ID = pf.id
         left join CONCESSAOFERIASLICENCA concessao on periodo.ID = concessao.PERIODOAQUISITIVOFL_ID
where basepa.TIPOPERIODOAQUISITIVO = 'FERIAS'
  and (periodo.STATUS = 'NAO_CONCEDIDO' or
       (periodo.STATUS = 'CONCEDIDO' and (concessao.MES > 5 and concessao.ANO >= 2023)))
  and (periodo.FINALVIGENCIA between to_date('01/01/2023', 'dd/MM/yyyy') and to_date('30/05/2023', 'dd/MM/yyyy'))
  and sysdate between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, sysdate)
  and not exists(select *
                 from LancamentoTercoFeriasAut terco
                          inner join PERIODOAQUISITIVOFL pasub on terco.PERIODOAQUISITIVOFL_ID = pasub.ID
                 where pasub.CONTRATOFP_ID = contratofp.id)
