insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > UNIFICAÇÃO DE PESSOA EM LOTE',
        '/comum/unificacao-pessoa-lote/edita.xhtml', 0, 'GERENCIAMENTO');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'UNIFICAÇÃO DE PESSOA EM LOTE',
        '/comum/unificacao-pessoa-lote/edita.xhtml',
        (select id from menu where label = 'GERENCIAMENTO DE RECURSOS'),
        300);
