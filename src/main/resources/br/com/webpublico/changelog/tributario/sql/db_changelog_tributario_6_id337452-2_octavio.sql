insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > FUNÇÃO DOS PARÂMETROS DE ITBI > LISTA FUNÇÃO',
       '/tributario/itbi/parametrositbi/funcao/lista.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/lista.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > FUNÇÃO DOS PARÂMETROS DE ITBI > EDITA FUNÇÃO',
       '/tributario/itbi/parametrositbi/funcao/edita.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/edita.xhtml');

insert into recursosistema
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > ITBI > FUNÇÃO DOS PARÂMETROS DE ITBI > VISUALIZA FUNÇÃO',
       '/tributario/itbi/parametrositbi/funcao/visualizar.xhtml', 0, 'TRIBUTARIO' from dual
where not exists(select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/itbi/parametrositbi/funcao/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

delete from menu where label = 'FUNÇÃO DOS PARÂMETROS DE ITBI' and caminho = '/tributario/itbi/parametrositbi/funcao/lista.xhtml';

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'FUNÇÃO DOS PARÂMETROS DE ITBI',
       '/tributario/itbi/parametrositbi/funcao/lista.xhtml',
       (select m.id from menu m
                             inner join menu p on m.pai_id = p.id
        where m.label = 'ITBI' and p.label = 'TRIBUTÁRIO'),
       10
from dual
where not exists(select id from menu where caminho = '/tributario/itbi/parametrositbi/funcao/lista.xhtml');
