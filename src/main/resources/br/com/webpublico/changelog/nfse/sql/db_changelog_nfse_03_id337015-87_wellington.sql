insert into pgcc (ID, CADASTROECONOMICO_ID, INICIOVIGENCIA, FIMVIGENCIA, CONTA,
                  DESDOBRAMENTO, NOME, DESCRICAODETALHADA, COSIF_ID)
select HIBERNATE_SEQUENCE.nextval,
       ce.id,
       to_date('01/01/2000', 'dd/MM/yyyy'),
       to_date('31/12/' || extract(year from current_date), 'dd/MM/yyyy'),
       ipc.conta,
       '00',
       ipc.descricao,
       ipc.descricao,
       ipc.cosif_id
from cadastroeconomico ce
         inner join planocontasinterno pc on pc.banco_id = ce.banco_id and pc.ativo = 1
         inner join itemplanocontasinterno ipc on ipc.planocontasinterno_id = pc.id
