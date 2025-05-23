insert into processorevisaocalculoiptuexercicio (id, processorevisaocalculoiptu_id, exercicio_id)
select hibernate_sequence.nextval, id, exercicio_id
from processorevisaocalculoiptu
