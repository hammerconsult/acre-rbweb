insert into recursosistema (id, nome, caminho, cadastro, modulo)
values(hibernate_sequence.nextval, 'RECURSOS HUMANOS > RELATÓRIOS - RH > PREVIDÊNCIA > RELATÓRIO INDIVIDUALIZADO DE PREVIDÊNCIA COMPLEMENTAR', '/rh/relatorios/previdencia/contribuicao-individualizado-complementar.xhtml', 0, 'RH');

insert into menu
(id, label, caminho, pai_id, ordem, icone)
values(hibernate_sequence.nextval, 'RELATÓRIO INDIVIDUALIZADO DE PREVIDÊNCIA COMPLEMENTAR', '/rh/relatorios/previdencia/contribuicao-individualizado-complementar.xhtml', 658783348, 55, null);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
values ((select id from gruporecurso where nome = 'Recursos Humanos'),
        (select id from recursosistema where nome = 'RECURSOS HUMANOS > RELATÓRIOS - RH > PREVIDÊNCIA > RELATÓRIO INDIVIDUALIZADO DE PREVIDÊNCIA COMPLEMENTAR'));
