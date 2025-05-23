merge into itemaquisicao itemaq using (
    select iaq.id, (select isa.id
                   from itemsolicitacaoaquisicao isa
                            inner join eventobem evsaq on evsaq.id = isa.id
                    where evsaq.estadoresultante_id = evaq.estadoinicial_id) as id_item_solicitacao
    from aquisicao aq
             inner join solicitacaoaquisicao solAq on solAq.id = aq.solicitacaoaquisicao_id
             inner join itemaquisicao iaq on iaq.aquisicao_id = aq.id
             inner join eventobem evaq on evaq.id = iaq.id
) dados on (dados.id = itemaq.id)
when matched then update set itemaq.itemsolicitacaoaquisicao_id = dados.id_item_solicitacao;
