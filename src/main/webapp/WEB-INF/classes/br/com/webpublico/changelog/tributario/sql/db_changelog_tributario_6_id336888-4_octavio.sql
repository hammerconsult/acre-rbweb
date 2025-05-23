insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO MOBILIÁRIO - C.M.C. > ALVARÁ > TABELAS BÁSICAS - ALVARÁ > LISTA RODAPÉ DE ALVARÁ',
       '/tributario/alvara/rodapealvara/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO MOBILIÁRIO - C.M.C. > ALVARÁ > TABELAS BÁSICAS - ALVARÁ > EDITA RODAPÉ DE ALVARÁ',
       '/tributario/alvara/rodapealvara/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/edita.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO TÉCNICO MOBILIÁRIO - C.M.C. > ALVARÁ > TABELAS BÁSICAS - ALVARÁ > VISUALIZA RODAPÉ DE ALVARÁ',
       '/tributario/alvara/rodapealvara/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/lista.xhtml')
                  and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/edita.xhtml')
                  and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/alvara/rodapealvara/visualizar.xhtml')
                  and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'RODAPÉ DO ALVARÁ',
       '/tributario/alvara/rodapealvara/lista.xhtml',
       (select m.id from menu m
                             inner join menu p on m.pai_id = p.id
        where m.label = 'TABELAS BÁSICAS - ALVARÁ' and p.label = 'ALVARÁ'),
       30
from dual
where not exists(select id from menu where caminho = '/tributario/alvara/rodapealvara/lista.xhtml');
