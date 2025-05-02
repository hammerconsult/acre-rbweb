merge into DOTACAOSOLMATITEMFONTE fonteParaAtualizar
    using (select fonte.id as idFonte,
                  ex.id    as idExercicio
           from DOTSOLMAT dot
                    inner join DOTACAOSOLMATITEM item on item.dotacaoSolicitacaoMaterial_id = dot.id
                    inner join DOTACAOSOLMATITEMFONTE fonte on fonte.dotacaoSolMatItem_id = item.id
                    inner join EXERCICIO ex on ex.ano = extract(year from dot.realizadaEm)
           where fonte.exercicio_id is null) dados on (fonteParaAtualizar.id = dados.idFonte)
    when matched then update set fonteParaAtualizar.exercicio_id = dados.idExercicio;
