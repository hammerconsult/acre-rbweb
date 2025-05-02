insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1005'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 1);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1057'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1015'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1074'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1017'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1018'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 'ADICAO', (select b.id from basefp b where b.codigo = '1019'), (select ev.id from eventofp ev where ev.codigo = '564'), 'NORMAL', 0);

update ITEMBASEFP item
set OPERACAOFORMULA = 'ADICAO',
    SOMAVALORRETROATIVO = 1
where item.EVENTOFP_ID = (select ev.id from eventofp ev where ev.codigo = '564')
  and item.BASEFP_ID = (select b.id from basefp b where b.codigo = '1003');

update ITEMBASEFP item
set OPERACAOFORMULA = 'ADICAO',
    SOMAVALORRETROATIVO = 1
where item.EVENTOFP_ID = (select ev.id from eventofp ev where ev.codigo = '564')
  and item.BASEFP_ID = (select b.id from basefp b where b.codigo = '1002');
