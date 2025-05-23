insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > CADEIA DOMINIAL DE ITBI > LISTA CADEIA DOMINIAL DE ITBI',
       '/tributario/itbi/cadeiadominial/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > CADEIA DOMINIAL DE ITBI > EDITA CADEIA DOMINIAL DE ITBI',
       '/tributario/itbi/cadeiadominial/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/edita.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > CADEIA DOMINIAL DE ITBI > VISUALIZA CADEIA DOMINIAL DE ITBI',
       '/tributario/itbi/cadeiadominial/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/cadeiadominial/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

delete from menu where label = 'LANÇAMENTO DE ITBI' and caminho = '/tributario/itbi/calculo/lista.xhtml';

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'CADEIA DOMINIAL DE ITBI',
       '/tributario/itbi/cadeiadominial/lista.xhtml',
       (select m.id from menu m
                             inner join menu p on m.pai_id = p.id
        where m.label = 'ITBI' and p.label = 'TRIBUTÁRIO'),
       40
from dual
where not exists(select id from menu where caminho = '/tributario/itbi/cadeiadominial/lista.xhtml');
