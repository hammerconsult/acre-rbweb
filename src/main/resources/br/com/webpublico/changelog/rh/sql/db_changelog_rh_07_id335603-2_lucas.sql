update PREVIDENCIAVINCULOFP
set FINALVIGENCIA = (select max(prev.INICIOVIGENCIA) - 1
                     from PREVIDENCIAVINCULOFP prev
                              inner join TIPOPREVIDENCIAFP tipo
                                         on prev.TIPOPREVIDENCIAFP_ID = tipo.ID and tipo.CODIGO = 5 and
                                            prev.CONTRATOFP_ID = PREVIDENCIAVINCULOFP.CONTRATOFP_ID)
where CONTRATOFP_ID in (select v.id
                        from vinculofp v
                                 inner join contratofp c on v.ID = c.ID
                        where v.ISENTOPREVIDENCIA = 1)
  and FINALVIGENCIA is null
  and TIPOPREVIDENCIAFP_ID not in (select t.id from TIPOPREVIDENCIAFP t where t.CODIGO = 5);

