insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ARRECADAÇÃO > RELATÓRIOS - ARRECADAÇÃO > ACOMPANHAMENTO DA RECEITA > RELATÓRIO DE LANÇAMENTO DE PROTESTO',
       '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into menu (id, caminho, label, pai_id, ordem)
select hibernate_sequence.nextval, '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml', 'RELATÓRIO DE LANÇAMENTO DE PROTESTO',
       (select m.id from menu m
        inner join menu p on m.pai_id = p.id
        where m.label = 'ACOMPANHAMENTO DA RECEITA' and p.label = 'RELATÓRIOS - ARRECADAÇÃO'), (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'ACOMPANHAMENTO DA RECEITA'))
from dual
where not exists(select id from menu where caminho = '/tributario/contacorrente/relatorio/relacaolancamentoprocessoprotesto.xhtml');
