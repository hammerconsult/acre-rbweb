update LANCAMENTOFP l
set l.MESANOFINAL=to_date('31/07/2020', 'dd/MM/yyyy')
where l.id in (
    select LANCAMENTOFP.id
    from vinculofp v
             inner join UNIDADEORGANIZACIONAL uo on v.UNIDADEORGANIZACIONAL_ID = uo.ID
             inner join HIERARQUIAORGANIZACIONAL h on uo.ID = h.SUBORDINADA_ID
             inner join lancamentofp on v.ID = LANCAMENTOFP.VINCULOFP_ID
    where h.CODIGO = '01.10.00.00000.000.00'
      and to_date('31/07/2020', 'dd/MM/yyyy') between h.inicioVigencia
      and coalesce(h.fimVigencia, to_date('31/07/2020', 'dd/MM/yyyy'))
      and to_date('31/07/2020', 'dd/MM/yyyy') between lancamentofp.mesAnoInicial
      and coalesce(lancamentofp.mesAnoFinal, to_date('31/07/2020', 'dd/MM/yyyy'))
      and LANCAMENTOFP.EVENTOFP_ID = 168441672
);
