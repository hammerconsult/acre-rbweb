insert into recursosistema
select hibernate_sequence.nextval,
       'EXPORTAÇÃO ARQUIVO BI > BI - CONFIGURAÇÃO DE TRIBUTOS BI',
       '/bi-exportacao/configuracao-tributario/edita.xhtml',
       0,
       'CADASTROS'
from dual
where not exists(select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/edita.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into recursosistema
select hibernate_sequence.nextval,
       'EXPORTAÇÃO ARQUIVO BI > BI - CONFIGURAÇÃO DE TRIBUTOS BI',
       '/bi-exportacao/configuracao-tributario/visualizar.xhtml',
       0,
       'CADASTROS'
from dual
where not exists(select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/visualizar.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/visualizar.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/visualizar.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into recursosistema
select hibernate_sequence.nextval,
       'EXPORTAÇÃO ARQUIVO BI > BI - CONFIGURAÇÃO DE TRIBUTOS BI',
       '/bi-exportacao/configuracao-tributario/lista.xhtml',
       0,
       'CADASTROS'
from dual
where not exists(select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/lista.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/bi-exportacao/configuracao-tributario/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'BI - CONFIGURAÇÃO DE TRIBUTOS BI',
       '/bi-exportacao/configuracao-tributario/lista.xhtml',
       886232444,
       30
from dual
where not exists(select id from menu where caminho = '/bi-exportacao/configuracao-tributario/lista.xhtml');
