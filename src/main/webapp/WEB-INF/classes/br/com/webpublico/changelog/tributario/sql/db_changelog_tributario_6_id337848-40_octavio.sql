insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > COMPOSIÇÃO DE DÍVIDA > CONFIGURAÇÃO DO PIX',
       '/tributario/configuracaopix/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/configuracaopix/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > COMPOSIÇÃO DE DÍVIDA > CONFIGURAÇÃO DO PIX',
       '/tributario/configuracaopix/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/configuracaopix/edita.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > TABELAS E CONFIGURAÇÕES GERAIS > COMPOSIÇÃO DE DÍVIDA > CONFIGURAÇÃO DO PIX',
       '/tributario/configuracaopix/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/configuracaopix/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/configuracaopix/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/configuracaopix/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/configuracaopix/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/configuracaopix/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/configuracaopix/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/configuracaopix/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


insert into menu (id, caminho, label, pai_id, ordem)
select hibernate_sequence.nextval, '/tributario/configuracaopix/lista.xhtml', 'CONFIGURAÇÃO DO PIX',
       (select m.id from menu m
        inner join menu p on m.pai_id = p.id
        where m.label = 'COMPOSIÇÃO DE DÍVIDA' and p.label = 'TABELAS E CONFIGURAÇÕES GERAIS'), (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'COMPOSIÇÃO DE DÍVIDA'))
from dual
where not exists(select id from menu where caminho = '/tributario/configuracaopix/lista.xhtml');
