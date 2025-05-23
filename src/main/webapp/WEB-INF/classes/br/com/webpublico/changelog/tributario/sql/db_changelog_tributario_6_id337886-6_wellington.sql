insert into processorevisaodividaativaexercicio (id, processorevisaodividaativa_id, exercicio_id)
select hibernate_sequence.nextval,
       id,
       exerciciodebito_id
from processorevisaodividaativa
