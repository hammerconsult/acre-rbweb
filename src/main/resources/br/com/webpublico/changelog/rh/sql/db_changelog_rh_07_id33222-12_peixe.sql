insert into eventoFPTipoFolha (id, eventofp_id, tipoFolhaDePagamento)
values (hibernate_sequence.nextval, (select e.id from eventofp e where e.codigo = '1104'), 'SALARIO_13');
