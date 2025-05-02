insert into PREVIDENCIAVINCULOFP(ID, FINALVIGENCIA, INICIOVIGENCIA, CONTRATOFP_ID, TIPOPREVIDENCIAFP_ID, ATOLEGAL_ID)
select HIBERNATE_SEQUENCE.nextval,
       c.FINALISENCAO,
       c.INICIOISENCAO,
       v.id,
       (select id from TIPOPREVIDENCIAFP where CODIGO = 5),
       c.ATOLEGALPREVIDENCIA_ID
from vinculofp v
         inner join contratofp c on v.ID = c.ID
where v.ISENTOPREVIDENCIA = 1
  and c.id not in (select contrato.id
                   from contratofp contrato
                            inner join PREVIDENCIAVINCULOFP previdencia on contrato.ID = previdencia.CONTRATOFP_ID
                            inner join TIPOPREVIDENCIAFP on previdencia.TIPOPREVIDENCIAFP_ID = TIPOPREVIDENCIAFP.ID
                   where c.INICIOISENCAO = previdencia.INICIOVIGENCIA
                     and TIPOPREVIDENCIAFP.CODIGO = 5
                     and c.id = contrato.id);
