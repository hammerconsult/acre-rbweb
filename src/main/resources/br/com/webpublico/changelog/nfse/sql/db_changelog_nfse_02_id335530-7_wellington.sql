insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'nota premiada  > usuários (nota premiada) > lista',
        '/tributario/nfse/notapremiada/usuario/lista.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'nota premiada  > usuários (nota premiada) > edita',
        '/tributario/nfse/notapremiada/usuario/edita.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'nota premiada  > usuários (nota premiada) > visualizar',
        '/tributario/nfse/notapremiada/usuario/visualizar.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
 and recursosistema.caminho like '/tributario/nfse/notapremiada/usuario/%'
 and not exists(select 1 from gruporecursosistema s
                where s.gruporecurso_id = gruporecurso.id
                  and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'USUARIOS (NOTA PREMIADA)',
        '/tributario/nfse/notapremiada/usuario/lista.xhtml',
        (select id from menu where label = 'NOTA PREMIADA'), 0);
