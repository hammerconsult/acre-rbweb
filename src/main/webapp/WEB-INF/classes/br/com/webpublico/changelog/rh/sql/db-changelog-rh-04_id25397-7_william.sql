insert into PENSAOATOLEGAL (id, pensao_id, atolegal_id)
select hibernate_sequence.nextval as id_nn, id, ATOLEGAL_ID from pensao;
