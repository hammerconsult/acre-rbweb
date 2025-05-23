insert into basefp(ID, CODIGO, DESCRICAO, DESCRICAOREDUZIDA, FILTROBASEFP)
values (HIBERNATE_SEQUENCE.nextval, 1555, 'BASE REMUNERAÇÃO ACOMPANHAMENTO FAMILIAR', 'BS REM ACOMP FAMILIAR', 'NORMAL');

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190487, 'INTEGRAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190150, 'INTEGRAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190146, 'NORMAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190166, 'INTEGRAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190508, 'INTEGRAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1555'), 6190321, 'INTEGRAL',0);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1002'), (select ev.id from eventofp ev where ev.codigo = '555'), 'NORMAL',1);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1003'), (select ev.id from eventofp ev where ev.codigo = '555'), 'NORMAL',1);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1001'), (select ev.id from eventofp ev where ev.codigo = '555'), 'NORMAL',1);

insert into ITEMBASEFP(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, TIPOVALOR, SOMAVALORRETROATIVO)
values (HIBERNATE_SEQUENCE.nextval, sysdate,'ADICAO', (select b.id from basefp b where b.codigo = '1005'), (select ev.id from eventofp ev where ev.codigo = '555'), 'NORMAL',1);
