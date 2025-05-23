insert into horariocontratofp (id, iniciovigencia, finalvigencia, horariodetrabalho_id) values (hibernate_sequence.nextval, to_date('21/11/2023', 'dd/MM/yyyy'), null,  639107941);
update lotacaofuncional set horariocontratofp_id = (select max(horario.id) from HORARIOCONTRATOFP horario where horario.iniciovigencia = to_date('21/11/2023', 'dd/MM/yyyy')) where id = 11008830134;
