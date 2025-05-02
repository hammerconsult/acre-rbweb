insert into ContaCorrenteBancaria (ID, DATAREGISTRO, DIGITOVERIFICADOR, NUMEROCONTA, SITUACAO, AGENCIA_ID, MODALIDADECONTA)
values (HIBERNATE_SEQUENCE.nextval, current_date, '2', '68845', 'ATIVO', 10903207475, 'CONTA_SALARIO');

insert into CONTACORRENTEBANCPESSOA(ID, CONTACORRENTEBANCARIA_ID, PESSOA_ID, PRINCIPAL)
values (HIBERNATE_SEQUENCE.nextval, (select max(conta.id)
                                     from ContaCorrenteBancaria conta
                                     where conta.DIGITOVERIFICADOR = 2
                                       and conta.NUMEROCONTA = 68845
                                       and conta.SITUACAO = 'ATIVO'
                                       and conta.AGENCIA_ID = 10903207475
                                       and conta.MODALIDADECONTA = 'CONTA_SALARIO'), 185376342, 1);

