update menu set ordem=102
where id=548885934;

insert into recursosistema
values (hibernate_sequence.nextval, 'RECURSOS HUMANOS > CONSULTA DE DADOS DE PROVA DE VIDA > EDITA',
        '/rh/consulta-provadevida/edita.xhtml', 0, 'RH');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'CONSULTA DE DADOS DE PROVA DE VIDA',
        '/rh/consulta-provadevida/edita.xhtml',-124756695, 101);
