insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('25/05/2015', 'dd/mm/yyyy'), to_date('25/05/2015', 'dd/mm/yyyy'), 4, 'RESCISAO', 8756991, 653492799, 33);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653492799 and VERSAO = 33), 639944925, 638918547, 0, 140778336);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653492799 and ficha.VINCULOFP_ID = 638918547 and folha.VERSAO = 33) where id = 653506801;

insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('25/05/2015', 'dd/mm/yyyy'), to_date('25/05/2015', 'dd/mm/yyyy'), 4, 'RESCISAO', 8756991, 653492799, 34);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653492799 and VERSAO = 34), 639944925, 638918547, 0, 140778336);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653492799 and ficha.VINCULOFP_ID = 638918547 and folha.VERSAO = 34) where id in (653506804, 653506805);

insert into folhadepagamento (id, ano, calculadaem, efetivadaem, mes, TIPOFOLHADEPAGAMENTO, UNIDADEORGANIZACIONAL_ID, COMPETENCIAFP_ID, VERSAO) values
  (hibernate_sequence.nextval, 2015, to_date('25/05/2015', 'dd/mm/yyyy'), to_date('25/05/2015', 'dd/mm/yyyy'), 4, 'RESCISAO', 8756991, 653492799, 49);

insert into FICHAFINANCEIRAFP (id, FOLHADEPAGAMENTO_ID, RECURSOfp_id, VINCULOFP_ID, CREDITOSALARIOPAGO, UNIDADEORGANIZACIONAL_ID) values
  (hibernate_sequence.nextval, (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653492799 and VERSAO = 49), 639944925, 638918547, 0, 140778336);

update ITEMFICHAFINANCEIRAFP set FICHAFINANCEIRAFP_ID = (select ficha.id from FICHAFINANCEIRAFP ficha
  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id
where folha.COMPETENCIAFP_ID = 653492799 and ficha.VINCULOFP_ID = 638918547 and folha.VERSAO = 49) where id in (653506809, 653506810);

delete from FICHAFINANCEIRAFP where FOLHADEPAGAMENTO_ID = (select id from FOLHADEPAGAMENTO where COMPETENCIAFP_ID = 653492799 and VERSAO = 99) and VINCULOFP_ID = 638918547;
