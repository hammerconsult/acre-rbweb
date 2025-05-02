insert into pgcc (id, cadastroeconomico_id, iniciovigencia, fimvigencia, conta,
                  desdobramento, nome, descricaodetalhada, cosif_id, servico_id)
select hibernate_sequence.nextval,
       cadastroeconomico_id,
       iniciovigencia,
       fimvigencia,
       conta,
       desdobramento,
       nome,
       descricaodetalhada,
       cosif_id,
       servico_id
from (
         select distinct ce.id         as cadastroeconomico_id,
                         case
                             when pc.ativo = 1 then to_date('01/01/' || extract(year from current_date), 'dd/MM/yyyy')
                             else to_date('01/01/' || extract(year from current_date) - 1, 'dd/MM/yyyy')
                             end       as iniciovigencia,
                         case
                             when pc.ativo = 1 then to_date('31/12/' || extract(year from current_date), 'dd/MM/yyyy')
                             else to_date('31/12/' || extract(year from current_date) - 1, 'dd/MM/yyyy')
                             end       as fimvigencia,
                         ipc.conta,
                         '00'          as desdobramento,
                         ipc.descricao as nome,
                         ipc.descricao as descricaodetalhada,
                         ipc.cosif_id,
                         ipc.servico_id
         from cadastroeconomico ce
                  inner join planocontasinterno pc
                             on pc.banco_id = ce.banco_id
                  inner join itemplanocontasinterno ipc on ipc.planocontasinterno_id = pc.id)
