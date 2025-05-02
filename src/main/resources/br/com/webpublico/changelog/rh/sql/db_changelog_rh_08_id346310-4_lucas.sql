insert into EventoFPTipoFolha(ID, EVENTOFP_ID, TIPOFOLHADEPAGAMENTO)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '564'), 'RESCISAO_COMPLEMENTAR');

insert into EventoFPTipoFolha(ID, EVENTOFP_ID, TIPOFOLHADEPAGAMENTO)
values (HIBERNATE_SEQUENCE.nextval, (select ev.id from eventofp ev where ev.codigo = '564'), 'MANUAL');
