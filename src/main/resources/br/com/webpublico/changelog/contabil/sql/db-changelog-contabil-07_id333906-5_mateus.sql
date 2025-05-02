insert into TipoConta(id, descricao, mascara, classeDaConta, exercicio_id) values (
  hibernate_sequence.nextval, 'Conta de Destinação de Recursos', '9.99.99', 'DESTINACAO', 278386613
);

update planodecontas set tipoconta_id = (select id from tipoconta where classeDaConta = 'DESTINACAO' and exercicio_id = 278386613)
where exercicio_id = 278386613 and DESCRICAO = 'Conta de Destinação de Recurso';
