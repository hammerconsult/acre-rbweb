insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'RECURSOS HUMANOS > RELATÓRIOS - RH > VIDA FUNCIONAL > RELATÓRIO DE IMPRESSÃO DE QRCODE DOS SERVIDORES',
        '/rh/relatorios/relatorioimpressaoqrcodeservidor.xhtml', 0, 'RH');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'RELATÓRIO DE IMPRESSÃO DE QRCODE DOS SERVIDORES',
        '/rh/relatorios/relatorioimpressaoqrcodeservidor.xhtml', (select m.id
                                                                  from menu m
                                                                           inner join menu p on p.id = m.pai_id
                                                                  where trim(p.label) = 'RELATÓRIOS - RH'
                                                                    and trim(m.label) = 'VIDA FUNCIONAL'),
        300);
