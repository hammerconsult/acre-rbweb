insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > LISTAR RETORNO REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > DÍVIDA ATIVA > PROCESSO DE PROTESTO > VER RETORNO REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/retorno/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'RETORNO REMESSA',
       '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml',
       (select m.id from menu m
                             inner join menu p on m.pai_id = p.id
        where m.label = 'PROCESSO DE PROTESTO' and p.label = 'DÍVIDA ATIVA'), 30
from dual
where not exists(select id from menu where caminho = '/tributario/dividaativa/processodeprotesto/remessa/retorno/lista.xhtml');
