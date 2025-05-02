insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CONTA CORRENTE > PROCESSO DE DÉBITO (LOTE)',
        '/tributario/contacorrente/processodebitolote/lista.xhtml', 1, 'TRIBUTARIO');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'PROCESSO DE DÉBITOS (LOTE)',
        '/tributario/contacorrente/processodebitolote/lista.xhtml',
        -126758256, (select max(ordem) + 10 from menu where pai_id = -126758256));

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/contacorrente/processodebitolote/lista.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
