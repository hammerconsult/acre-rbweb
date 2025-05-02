insert into configuracaoaposentadoria
(id, regraaposentadoria)
values(hibernate_sequence.nextval, 'INVALIDEZ_ART23_2009');

insert into tempominimoaposentadoria
(id, configuracaoaposentadoria_id, sexo, quantidademinima)
values(hibernate_sequence.nextval, (select max(id) from configuracaoaposentadoria) , 'FEMININO', 1);

insert into tempominimoaposentadoria
(id, configuracaoaposentadoria_id, sexo, quantidademinima)
values(hibernate_sequence.nextval, (select max(id) from configuracaoaposentadoria) , 'MASCULINO', 1);
