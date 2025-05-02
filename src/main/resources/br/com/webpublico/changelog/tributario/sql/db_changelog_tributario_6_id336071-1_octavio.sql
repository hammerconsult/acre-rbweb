update cadastroeconomico cmc
set cmc.classificacaoatividade = (select ef.classificacaoatividade from enquadramentofiscal ef
                                  where ef.cadastroeconomico_id = cmc.id
                                  and ef.fimvigencia is null
                                  order by ef.id desc fetch first 1 rows only);
