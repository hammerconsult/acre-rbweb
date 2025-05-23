insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > CADASTRO MUNICIPAL > BLOQUEIO DE TRANSFERÊNCIA DE PROPRIETÁRIO > LISTA',
        '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml', 1, 'TRIBUTARIO');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > CADASTRO MUNICIPAL > BLOQUEIO DE TRANSFERÊNCIA DE PROPRIETÁRIO > EDITA',
        '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/edita.xhtml', 1, 'TRIBUTARIO');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > CADASTRO MUNICIPAL > BLOQUEIO DE TRANSFERÊNCIA DE PROPRIETÁRIO > VISUALIZAR',
        '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/visualizar.xhtml', 1, 'TRIBUTARIO');

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'BLOQUEIO DE TRANSFERÊNCIA DE PROPRIETÁRIO',
        '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml',
        (select id from menu where label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.'),
        (select max(ordem) from menu where label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.') + 10);

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml'
  and not exists(select 1
                 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);
