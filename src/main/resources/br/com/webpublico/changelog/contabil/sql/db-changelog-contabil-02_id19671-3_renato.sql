update periodofase pf set
pf.iniciovigencia = (select max(iniciovigencia) from periodofaseunidade where periodofase_id = pf.id),
pf.fimvigencia = (select max(fimvigencia) from periodofaseunidade where periodofase_id = pf.id)


