-- Valor do Serviço
insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1161
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4000'));

insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1161
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4020'));

-- Valor Patronal
insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1162
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4154'));

insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1162
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4174'));

-- INSS
insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1163
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4050'));

insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1163
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4070'));

-- SEST
insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1164
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4080'));

-- SENAT
insert into itemgrupoexportacao(id, operacaoformula, grupoexportacao_id, eventofp_id)
values (hibernate_sequence.nextval, 'ADICAO', (select grupo.id
                                               from grupoexportacao grupo
                                                        inner join moduloexportacao modulo on grupo.moduloexportacao_id = modulo.id
                                               where grupo.codigo = 1165
                                                 and modulo.codigo = 11),
        (select ev.id from eventofp ev where ev.codigo = '4081'));
