update PeriodoAquisitivoFL p
set p.saldo = 0,
    p.saldodedireito = 30,
    p.STATUS = 'CONCEDIDO'
where p.CONTRATOFP_ID  = 882717022
  and p.INICIOVIGENCIA = TO_DATE('31/05/2023', 'dd/mm/yyyy')
  and p.FINALVIGENCIA = TO_DATE('30/05/2024', 'dd/mm/yyyy')
  and p.SALDODEDIREITO = 45;
