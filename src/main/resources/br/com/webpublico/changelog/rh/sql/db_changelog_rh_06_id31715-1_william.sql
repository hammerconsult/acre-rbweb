insert into referenciafp values (hibernate_sequence.nextval, 'Valor PEQ Magistério', 'VALOR_VALOR', '33');
insert into referenciafp values (hibernate_sequence.nextval, 'Valor PEQ Apoio', 'VALOR_VALOR', '34');
insert into ValorReferenciaFP values (hibernate_sequence.nextval, to_date('01/04/2020', 'dd/MM/yyyy') , null, to_date('01/04/2020', 'dd/MM/yyyy'), null,
                                      (select id from referenciafp where codigo = '33'));
insert into ValorReferenciaFP values (hibernate_sequence.nextval, to_date('01/04/2020', 'dd/MM/yyyy') , null, to_date('01/04/2020', 'dd/MM/yyyy'), null,
                                      (select id from referenciafp where codigo = '34'));
