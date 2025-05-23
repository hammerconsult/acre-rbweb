insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > LISTAR REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > VER REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/visualizar.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > NOVA REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/edita.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

update menu set caminho = null
where caminho = '/tributario/dividaativa/processodeprotesto/lista.xhtml';

insert into menu (id, caminho, label, pai_id, ordem)
select hibernate_sequence.nextval, '/tributario/dividaativa/processodeprotesto/lista.xhtml', 'PROTESTAR DÉBITOS',
       (select m.id from menu m
        inner join menu p on m.pai_id = p.id
        where m.label = 'PROCESSO DE PROTESTO' and p.label = 'DÍVIDA ATIVA'), 10
from dual
where not exists(select id from menu where caminho = '/tributario/dividaativa/processodeprotesto/lista.xhtml');

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml',
       (select m.id from menu m
        inner join menu p on m.pai_id = p.id
        where m.label = 'PROCESSO DE PROTESTO' and p.label = 'DÍVIDA ATIVA'), 20
from dual
where not exists(select id from menu where caminho = '/tributario/dividaativa/processodeprotesto/remessa/lista.xhtml');
