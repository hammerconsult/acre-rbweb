insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'termo de uso > lista',
        '/comum/termouso/lista.xhtml', 0, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'termo de uso > edita',
        '/comum/termouso/edita.xhtml', 0, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'termo de uso > visualizar',
        '/comum/termouso/visualizar.xhtml', 0, 'CADASTROS');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho like '/comum/termouso/%'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'TERMO DE USO',
        '/comum/termouso/lista.xhtml',
        (select id from menu where label = 'CADASTROS GERAIS'), 300);
