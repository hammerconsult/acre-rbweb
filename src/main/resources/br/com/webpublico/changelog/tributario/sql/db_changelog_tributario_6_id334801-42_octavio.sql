insert into recursosistema
select hibernate_sequence.nextval,
       'Consulta de Alterações de CNAE/Endereço do Cadastro Econômico',
       '/tributario/alvara/alteracoes/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists(select id from recursosistema where caminho = '/tributario/alvara/alteracoes/edita.xhtml');

insert into gruporecursosistema
select (select id from recursosistema where caminho = '/tributario/alvara/alteracoes/edita.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/tributario/alvara/alteracoes/edita.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'CONSULTA DE ALTERAÇÕES DE CNAE/ENDEREÇO DO C.M.C',
       '/tributario/alvara/alteracoes/edita.xhtml',
       (select m.id from menu m
                             inner join menu p on m.pai_id = p.id
        where m.label = 'CÁLCULOS' and p.label = 'ALVARÁ'),
       30
from dual
where not exists(select id from menu where caminho = '/tributario/alvara/alteracoes/edita.xhtml');
