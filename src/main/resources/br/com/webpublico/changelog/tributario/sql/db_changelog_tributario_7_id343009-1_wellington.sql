insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO MUNICIPAL > SINCRONIZAÇÃO REDESIM (LOTE)',
        '/tributario/cadastromunicipal/sincronizacaoredesim/lista.xhtml', 1, 'TRIBUTARIO');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'SINCRONIZAÇÃO REDESIM (LOTE)',
        '/tributario/cadastromunicipal/sincronizacaoredesim/lista.xhtml',
        (select id from menu where label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.'), 300);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/cadastromunicipal/sincronizacaoredesim/lista.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
