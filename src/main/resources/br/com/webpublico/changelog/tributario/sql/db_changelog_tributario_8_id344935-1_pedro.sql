update PERMISSIONARIO
set FINALVIGENCIA = sysdate
where PERMISSAOTRANSPORTE_ID = 194844816;

insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 636138620,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 636138620), 0, (select REFERENCIA
                                                                         from SITUACAOPARCELAVALORDIVIDA
                                                                         WHERE PARCELA_ID = 636138620
                                                                         ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 636138620;


insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 760638000,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 760638000), 0, (select REFERENCIA
                                                                         from SITUACAOPARCELAVALORDIVIDA
                                                                         WHERE PARCELA_ID = 760638000
                                                                         ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 760638000;


insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 859871600,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 859871600), 0, (select REFERENCIA
                                                                         from SITUACAOPARCELAVALORDIVIDA
                                                                         WHERE PARCELA_ID = 859871600
                                                                         ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 859871600;


insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 10714676308,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 10714676308), 0, (select REFERENCIA
                                                                           from SITUACAOPARCELAVALORDIVIDA
                                                                           WHERE PARCELA_ID = 10714676308
                                                                           ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 10714676308;


insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 10764592609,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 10764592609), 0, (select REFERENCIA
                                                                           from SITUACAOPARCELAVALORDIVIDA
                                                                           WHERE PARCELA_ID = 10764592609
                                                                           ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 10764592609;


insert into SITUACAOPARCELAVALORDIVIDA (ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, INCONSISTENTE,
                                        REFERENCIA)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'CANCELAMENTO', 10894856529,
        (SELECT VALOR FROM PARCELAVALORDIVIDA WHERE ID = 10894856529), 0, (select REFERENCIA
                                                                           from SITUACAOPARCELAVALORDIVIDA
                                                                           WHERE PARCELA_ID = 10894856529
                                                                           ORDER BY ID DESC FETCH FIRST 1 ROW ONLY));

update PARCELAVALORDIVIDA
set SITUACAOATUAL_ID = HIBERNATE_SEQUENCE.currval
where id = 10894856529;
