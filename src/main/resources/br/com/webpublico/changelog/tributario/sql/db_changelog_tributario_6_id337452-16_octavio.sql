merge into retificacaocalculoitbi retificacao
using (select pcitbi.id as id, pcitbi.usuarioretificacao_id as usuario_retificacao,
              pcitbi.dataretificacao, pcitbi.motivoretificacao
       from processocalculoitbi pcitbi
       where pcitbi.usuarioretificacao_id is not null) processo
on (retificacao.processocalculoitbi_id = processo.id)
when not matched then insert (id, processocalculoitbi_id, usuarioretificacao_id, numeroretificacao, motivoretificacao, dataretificacao)
values (hibernate_sequence.nextval, processo.id, processo.usuario_retificacao, 1, processo.motivoretificacao, processo.dataretificacao)
