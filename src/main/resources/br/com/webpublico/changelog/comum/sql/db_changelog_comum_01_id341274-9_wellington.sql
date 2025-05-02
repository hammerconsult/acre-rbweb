insert into gruporecursosistema
select (select id from recursosistema where caminho = '/comum/templateemail/lista.xhtml'),
       (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO') from dual
where not exists (select 1 from gruporecursosistema
                  where recursosistema_id = (select id from recursosistema where caminho = '/comum/templateemail/lista.xhtml')
                    and gruporecurso_id = (select id from gruporecurso where nome = 'ADMINISTRADOR TRIBUTÁRIO'));
