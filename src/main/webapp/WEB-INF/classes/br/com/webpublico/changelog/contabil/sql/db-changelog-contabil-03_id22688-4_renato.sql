update tipoconta set exercicio_id = (select id from exercicio where ano = 2017);

--2016
insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'RECEITA','Conta de Receita', '9.9.9.9.99.99.99.99',(select id from exercicio where ano = 2016));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'EXTRAORCAMENTARIA','Conta Extraorçamentária', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2016));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESTINACAO','Conta de Destinação de Recursos', '9.9',(select id from exercicio where ano = 2016));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'CONTABIL','Conta Contábil', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2016));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESPESA','Conta de Despesa', '9.9.99.99.99.99.99',(select id from exercicio where ano = 2016));

--2015
insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'RECEITA','Conta de Receita', '9.9.9.9.99.99.99.99',(select id from exercicio where ano = 2015));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'EXTRAORCAMENTARIA','Conta Extraorçamentária', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2015));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESTINACAO','Conta de Destinação de Recursos', '9.9',(select id from exercicio where ano = 2015));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'CONTABIL','Conta Contábil', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2015));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESPESA','Conta de Despesa', '9.9.99.99.99.99.99',(select id from exercicio where ano = 2015));


--2014
insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'RECEITA','Conta de Receita', '9.9.9.9.99.99.99.99',(select id from exercicio where ano = 2014));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'EXTRAORCAMENTARIA','Conta Extraorçamentária', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2014));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESTINACAO','Conta de Destinação de Recursos', '9.9',(select id from exercicio where ano = 2014));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'CONTABIL','Conta Contábil', '9.9.9.9.9.99.99',(select id from exercicio where ano = 2014));

insert into tipoconta (id, classedaconta, descricao, mascara, exercicio_id)
values
(hibernate_sequence.nextval, 'DESPESA','Conta de Despesa', '9.9.99.99.99.99.99',(select id from exercicio where ano = 2014));



