merge into horariofunccalculoalvara horario
using (select distinct hf.horariofuncionamento_id, pca2.id as processo2022_id from processocalculoalvara pca
       inner join exercicio ex on pca.exercicio_id = ex.id
       inner join processocalculoalvara pca2 on pca.alvara_id = pca2.alvara_id
       inner join exercicio ex2 on pca2.exercicio_id = ex2.id
       inner join horariofunccalculoalvara hf on pca.id = hf.processocalculoalvara_id
       where pca.situacaocalculoalvara <> 'ESTORNADO'
       and pca2.situacaocalculoalvara <> 'ESTORNADO'
       and ex.ano = 2021
       and ex2.ano = 2022
       and pca.codigo = pca2.codigo
       and pca.cadastroeconomico_id = pca2.cadastroeconomico_id) dados
on (horario.processocalculoalvara_id = dados.processo2022_id)
when not matched then insert (id, processocalculoalvara_id, horariofuncionamento_id)
                      values (hibernate_sequence.nextval, dados.processo2022_id, dados.horariofuncionamento_id);
