
-- 183547/1 - lilian campos de pinho almeida de souza	638919443
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941, null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758858, null, null, 638919443, null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758858 where f.vinculofp_id = 638919443 and f.datacriacao > to_date('01/01/2024');

--700191/1 - paula cristina de souza da costa	639009055
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758858, null, null, 639009055, null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758858 where f.vinculofp_id = 639009055 and f.datacriacao > to_date('01/01/2024');

--700021/1 - james klinger menezes da silva	639007459
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758942 , null, null, 639007459 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758942 where f.vinculofp_id = 639007459  and f.datacriacao > to_date('01/01/2024');

--713200/2 - edmilson lima da silva	10664674757
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 6192025 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10664674757 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10664674757 and f.datacriacao > to_date('01/01/2024');

--714878/1 - anderson cleiton lima de moura	10958750949
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 11004761350 , null, null, 10958750949 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 11004761350 where f.vinculofp_id = 10958750949 and f.datacriacao > to_date('01/01/2024');

--700117/1 - francisco de jesus magalhaes braga	639008373
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 6192025 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758938 , null, null, 639008373 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758938 where f.vinculofp_id = 639008373 and f.datacriacao > to_date('01/01/2024');

--4707/1 - maria alzira rodrigues de lima	638890339
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638890339 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638890339 and f.datacriacao > to_date('01/01/2024');

--713267/1 - francisco jose souza do nascimento	10664539105
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10664539105 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10664539105 and f.datacriacao > to_date('01/01/2024');

--713238/2 - jose de souza matos	10851355556
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10851355556 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10851355556 and f.datacriacao > to_date('01/01/2024');

--21024/1 - luiza araujo da silva	638908534
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638908534 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638908534 and f.datacriacao > to_date('01/01/2024');

--182770/1 - valdenira da silva moraes	638919274
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 898500891 , null, null, 638919274 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 898500891 where f.vinculofp_id = 638919274 and f.datacriacao > to_date('01/01/2024');

--55409/1 - maria do socorro de carvalho ribeiro	638913658
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638913658 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638913658 and f.datacriacao > to_date('01/01/2024');

--713919/1 - enoque pereira de lima	10798703655
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 11005018286 , null, null, 10798703655 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 11005018286 where f.vinculofp_id = 10798703655 and f.datacriacao > to_date('01/01/2024');

--713116/1 - luan da silva dias	10649198624
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 897444007 , null, null, 10649198624 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 897444007 where f.vinculofp_id = 10649198624 and f.datacriacao > to_date('01/01/2024');

--6564/1 - josemir nogueira calixto	638892184
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 898500920 , null, null, 638892184 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 898500920 where f.vinculofp_id = 638892184 and f.datacriacao > to_date('01/01/2024');

--713298/2 - jose edmilson costa de souza	10800634899
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10800634899 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10800634899 and f.datacriacao > to_date('01/01/2024');

--713814/1 - francisco ferreira marinho	10788467475
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10788467475 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10788467475 and f.datacriacao > to_date('01/01/2024');

--700164/1 - matusalem diassis de souza	639008798
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758938 , null, null, 639008798 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758938 where f.vinculofp_id = 639008798 and f.datacriacao > to_date('01/01/2024');

--713089/1 - francisco das chagas fernandes franca	10649162011
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 6192025 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 10649162011 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 10649162011 and f.datacriacao > to_date('01/01/2024');

--6874/1 - maria eunice de alencar nascimento	638892453
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758631 , null, null, 638892453 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758631 where f.vinculofp_id = 638892453 and f.datacriacao > to_date('01/01/2024');

--704393/1 - mardilson machado torres	639057724
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 6192025 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758631 , null, null, 639057724 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758631 where f.vinculofp_id = 639057724 and f.datacriacao > to_date('01/01/2024');

--6890/1 - francisco antonio de souza nascimento	638892474
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 11005540592 , null, null, 638892474 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 11005540592 where f.vinculofp_id = 638892474 and f.datacriacao > to_date('01/01/2024');

--25003/1 - carlos nascimento barbosa	638912165
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638912165 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638912165 and f.datacriacao > to_date('01/01/2024');

--5398/1 - joao da silva ferreira	638891068
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638891068 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638891068 and f.datacriacao > to_date('01/01/2024');

--714453/2 - rhuslaynne de azevedo alves	10991634847
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 11004761350 , null, null, 10991634847 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 11004761350 where f.vinculofp_id = 10991634847 and f.datacriacao > to_date('01/01/2024');

--703805/1 - raquel maria de paiva souza	639049563
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758259 , null, null, 639049563 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758259 where f.vinculofp_id = 639049563 and f.datacriacao > to_date('01/01/2024');

--23680/1 - maria do socorro torres de araujo	638911144
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 595845804 , null, null, 638911144 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 595845804 where f.vinculofp_id = 638911144 and f.datacriacao > to_date('01/01/2024');

--55565/1 - antonio jose pereira	638913818
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 638913818 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 638913818 and f.datacriacao > to_date('01/01/2024');

--703266/1 - jose antonio sarmento junior	639044065
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 881191016 , null, null, 639044065 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 881191016 where f.vinculofp_id = 639044065 and f.datacriacao > to_date('01/01/2024');

--55743/1 - raimunda maria de queiroz silva	638913944
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758631 , null, null, 638913944 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758631 where f.vinculofp_id = 638913944 and f.datacriacao > to_date('01/01/2024');

--713205/3 - jose oliveira dos santos	10991633588
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 58758631 , null, null, 10991633588 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 58758631 where f.vinculofp_id = 10991633588 and f.datacriacao > to_date('01/01/2024');

--231843/1 - valquiria da silva cabral    638925995
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, null, to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, null, to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 898500920 , null, null, 638925995 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 898500920 where f.vinculofp_id = 638925995 and f.datacriacao > to_date('01/01/2024');

--700146/1 - adriana cristina silva loureiro    639008627
insert into horariocontratofp
(id, finalvigencia, iniciovigencia, horariodetrabalho_id, horariocontratofphist_id)
values(hibernate_sequence.nextval, to_date('23/04/2024', 'dd/mm/yyyy'), to_date('01/01/2024', 'dd/mm/yyyy'), 639107941 , null);

insert into lotacaofuncional
(id, dataregistro, finalvigencia, iniciovigencia, horariocontratofp_id, unidadeorganizacional_id, retornocedenciacontratofp_id, provimentofp_id, vinculofp_id, lotacaofuncionalhist_id)
values(hibernate_sequence.nextval, current_date, to_date('23/04/2024', 'dd/mm/yyyy'), to_date('01/01/2024', 'dd/mm/yyyy'), (select max(id) from horariocontratofp), 11005018286 , null, null, 639008627 , null);

update fichafinanceirafp f set unidadeorganizacional_id = 11005018286 where f.datacriacao between to_date('01/01/2024') and to_date('30/04/2024', 'dd/mm/yyyy') and f.vinculofp_id = 639008627;
