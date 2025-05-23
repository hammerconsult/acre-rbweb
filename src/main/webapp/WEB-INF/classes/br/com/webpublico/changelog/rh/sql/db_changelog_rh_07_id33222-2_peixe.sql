insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'NORMAL' );
insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'COMPLEMENTAR' );
insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'SALARIO_13' );
insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'RESCISAO' );
insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'ADIANTAMENTO_13_SALARIO' );
insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento) values (hibernate_sequence.nextval,(select e.id from eventofp e where e.codigo = '1100'),'RESCISAO_COMPLEMENTAR' );
