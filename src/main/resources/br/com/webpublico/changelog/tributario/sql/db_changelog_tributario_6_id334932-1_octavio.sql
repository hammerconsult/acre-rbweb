merge into parcelavalordivida residual using
                                     (select residual.id as id_residual, pvd.sequenciaparcela as sequencia_original,
                                             (select coalesce(p.quantidadeparcela, count(p.id))
                                              from parcelavalordivida p
                                              inner join valordivida v on v.id = p.valordivida_id
                                              where v.id = pvd.valordivida_id
                                              and p.opcaopagamento_id = pvd.opcaopagamento_id
                                              group by p.quantidadeparcela) as quantidade_original
                                      from parcelacontacorrentetrib parcela_cct
                                      inner join calculocontacorrente calc_cc on parcela_cct.calculocontacorrente_id = calc_cc.id
                                      inner join calculo calc on calc.id = calc_cc.id
                                      inner join valordivida vd on calc.id = vd.calculo_id
                                      inner join parcelavalordivida pvd on parcela_cct.parcelavalordivida_id = pvd.id
                                      inner join parcelavalordivida residual on residual.valordivida_id = vd.id
                                      where coalesce(pvd.sequenciaparcela, '0') <> coalesce(calc_cc.sequenciaparcela, '0')) original
on(residual.id = original.id_residual)
when matched then update set residual.sequenciaparcela = original.sequencia_original,
                             residual.quantidadeparcela = original.quantidade_original;
