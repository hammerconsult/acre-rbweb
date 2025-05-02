insert into recursosistema
values (hibernate_sequence.nextval, 'RECURSOS HUMANOS > PREVIDÊNCIA DO SERVIDOR > REGRAS APOSENTADORIA > LISTA',
        '/rh/administracaodepagamento/regraaposentadoria/lista.xhtml', 0, 'RH');

insert into recursosistema
values (hibernate_sequence.nextval, 'RECURSOS HUMANOS > PREVIDÊNCIA DO SERVIDOR > REGRAS APOSENTADORIA > EDITA',
        '/rh/administracaodepagamento/regraaposentadoria/edita.xhtml', 0, 'RH');

insert into recursosistema
values (hibernate_sequence.nextval, 'RECURSOS HUMANOS > PREVIDÊNCIA DO SERVIDOR > REGRAS APOSENTADORIA > VISIUALIZAR',
        '/rh/administracaodepagamento/regraaposentadoria/visualizar.xhtml', 0, 'RH');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'REGRAS APOSENTADORIA',
        '/rh/administracaodepagamento/regraaposentadoria/lista.xhtml',
        (select id from menu where label = 'PREVIDÊNCIA DO SERVIDOR'), 25);
