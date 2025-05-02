update ENQUADRAMENTOFUNCIONAL eq
set eq.CONSIDERAPARAPROGAUTOMAT = 0
where id in (select distinct enquadramento.id
             from EnquadramentoFuncional enquadramento
                      inner join CATEGORIAPCS cat on enquadramento.CATEGORIAPCS_ID = cat.ID
                      inner join PROGRESSAOPCS prog on enquadramento.PROGRESSAOPCS_ID = prog.ID
                      inner join categoriapcs catPai on catPai.id = cat.SUPERIOR_ID
                      inner join progressaopcs progPai on progPai.id = prog.SUPERIOR_ID
             where catPai.PLANOCARGOSSALARIOS_ID in (10804695037, 10809303895)
               and enquadramento.INICIOVIGENCIA = (select min(enq.INICIOVIGENCIA)
                                                   from EnquadramentoFuncional enq
                                                            inner join CATEGORIAPCS c on enq.CATEGORIAPCS_ID = c.ID
                                                            inner join PROGRESSAOPCS p on enq.PROGRESSAOPCS_ID = p.ID
                                                            inner join categoriapcs cPai on cPai.id = c.SUPERIOR_ID
                                                            inner join progressaopcs pPai on pPai.id = p.SUPERIOR_ID
                                                   where cPai.PLANOCARGOSSALARIOS_ID in (10804695037, 10809303895)
                                                     and enq.CONTRATOSERVIDOR_ID = enquadramento.CONTRATOSERVIDOR_ID))
