insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > IPTU > PROCESSO DE COMPENSAÇÃO > LISTAR',
        '/tributario/iptu/processo-compensacao/lista.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > IPTU > PROCESSO DE COMPENSAÇÃO > EDITAR',
        '/tributario/iptu/processo-compensacao/edita.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO IMOBILIÁRIO > IPTU > PROCESSO DE COMPENSAÇÃO > VER',
        '/tributario/iptu/processo-compensacao/visualizar.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/iptu/processo-compensacao/lista.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/iptu/processo-compensacao/edita.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/iptu/processo-compensacao/visualizar.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'PROCESSO DE COMPENSAÇÃO DE IPTU',
        '/tributario/iptu/processo-compensacao/lista.xhtml',
        (select id from menu where trim(label) = 'CÁLCULO DE I.P.T.U.' and caminho is null), 60);
