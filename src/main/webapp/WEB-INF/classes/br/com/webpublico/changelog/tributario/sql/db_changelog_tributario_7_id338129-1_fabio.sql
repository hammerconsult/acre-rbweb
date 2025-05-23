insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > RELATÓRIOS DE CÁLCULO DE IPTU > RELATÓRIO COMPARATIVO DE LANÇAMENTO DE IPTU',
        '/tributario/relatorio/comparativo-debitos-calculados.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/relatorio/comparativo-debitos-calculados.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'RELATÓRIO COMPARATIVO DE LANÇAMENTO DE IPTU',
        '/tributario/relatorio/comparativo-debitos-calculados.xhtml',
        (select id from menu where trim(label) = 'RELATÓRIOS DE CÁLCULO DE IPTU' and caminho is null), 0);
