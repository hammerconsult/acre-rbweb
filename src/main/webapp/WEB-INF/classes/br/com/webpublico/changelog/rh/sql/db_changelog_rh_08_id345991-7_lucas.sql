update lancamentotercoferiasaut lanc
set contratofp_id = (select pa.contratofp_id
                     from lancamentotercoferiasaut lancamento
                              inner join periodoaquisitivofl pa on lancamento.periodoaquisitivofl_id = pa.id
                     where lancamento.id = lanc.id)
where lanc.periodoaquisitivofl_id is not null;
