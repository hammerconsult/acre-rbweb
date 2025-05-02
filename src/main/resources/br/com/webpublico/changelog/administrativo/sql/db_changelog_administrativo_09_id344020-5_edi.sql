merge into ajusteprocessocompra aj
    using (select aj.id                                                                    as id_ajuste,
                  case
                      when cr.id is not null then cr.regsolmatext_id
                      when cl.id is not null then cl.licitacao_id
                      when cd.id is not null then cd.dispensadelicitacao_id end            as id_processo,
                  case
                      when cr.id is not null then 'ADESAO_EXTERNA'
                      when cl.id is not null then 'LICITACAO'
                      when cd.id is not null then 'DISPENSA_LICITACAO_INEXIGIBILIDADE' end as tipo_processo
           from ajusteprocessocompra aj
                    inner join contrato c on c.id = aj.idprocesso
                    left join conregprecoexterno cr on cr.contrato_id = c.id
                    left join conlicitacao cl on cl.contrato_id = c.id
                    left join condispensalicitacao cd on cd.contrato_id = c.id
           where tipoprocesso = 'CONTRATO') dados
    on (dados.id_ajuste = aj.id)
    when matched then
        update
            set aj.idprocesso   = dados.id_processo,
                aj.tipoprocesso = dados.tipo_processo;
