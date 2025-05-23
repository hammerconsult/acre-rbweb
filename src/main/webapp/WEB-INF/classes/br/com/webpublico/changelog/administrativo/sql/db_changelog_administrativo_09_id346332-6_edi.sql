merge into DOTACAOSOLMATITEMFONTE fonteParaAtualizar
    using (select fonte.id             as idFonte,
                  dot.realizadaEm      as data_lancamento,
                  dot.id               as id_origem,
                  dot.integroucontabil as gera_reserva,
                  ex.id                as id_exercicio
           from DOTSOLMAT dot
                    inner join DOTACAOSOLMATITEM item on item.dotacaoSolicitacaoMaterial_id = dot.id
                    inner join DOTACAOSOLMATITEMFONTE fonte on fonte.dotacaoSolMatItem_id = item.id
                    inner join EXERCICIO ex on ex.ano = extract(year from dot.realizadaEm)
           where fonte.datalancamento is null) dados
    on (fonteParaAtualizar.id = dados.idFonte)
    when matched then
        update
            set fonteParaAtualizar.datalancamento = dados.data_lancamento,
                fonteParaAtualizar.idorigem = dados.id_origem,
                fonteParaAtualizar.exercicio_id = dados.id_exercicio,
                fonteParaAtualizar.gerareservaorc = dados.gera_reserva
