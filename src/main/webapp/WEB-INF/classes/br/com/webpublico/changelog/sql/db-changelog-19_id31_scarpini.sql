update dam set sequencia = 1;                   

update dam set sequencia = 2 where id in (select id from (select max(dam.id) id, dam.numero, dam.exercicio_id 
   from dam 
group by dam.numero, dam.exercicio_id 
having count(dam.id)> 1));

ALTER TABLE dam
DROP CONSTRAINT UK_DAM_NUMERODAM;

ALTER TABLE dam
ADD CONSTRAINT UK_DAM_NUMERODAM UNIQUE (numero, exercicio_id, sequencia);