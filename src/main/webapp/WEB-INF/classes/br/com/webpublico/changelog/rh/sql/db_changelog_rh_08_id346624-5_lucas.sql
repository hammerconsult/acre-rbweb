insert into eventofptipofolha(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '555'), 'NORMAL');

insert into eventofptipofolha(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '555'), 'COMPLEMENTAR');

insert into eventofptipofolha(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '555'), 'RESCISAO');

insert into eventofptipofolha(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '555'), 'RESCISAO_COMPLEMENTAR');

insert into eventofptipofolha(id, eventofp_id, tipofolhadepagamento)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '555'), 'MANUAL');

insert into eventofpempregador (id, entidade_id, iniciovigencia, eventofp_id, identificacaotabela, naturezarubrica_id,
                                incidenciaprevidencia_id, incidenciatributariairrf_id, incidenciatributariafgts_id,
                                incidenciatributariarpps_id, tetoremuneratorio)
values (HIBERNATE_SEQUENCE.nextval, 8756990, to_date('01/01/2025', 'dd/MM/yyyy'),
        (select ev.id from eventofp ev where ev.codigo = '555'), 'MPRBRUB', 803879964, 803705760, 803705790, 803705835,
        803705841, 1);
