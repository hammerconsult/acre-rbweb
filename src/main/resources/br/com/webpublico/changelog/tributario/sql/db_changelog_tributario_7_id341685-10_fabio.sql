insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CONTA CORRENTE > PARCELAMENTO > BLOQUEIO DE PARCELAMENTO > LISTAR',
        '/tributario/contacorrente/bloqueioparcelamento/lista.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CONTA CORRENTE > PARCELAMENTO > BLOQUEIO DE PARCELAMENTO > EDITAR',
        '/tributario/contacorrente/bloqueioparcelamento/edita.xhtml', 0, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CONTA CORRENTE > PARCELAMENTO > BLOQUEIO DE PARCELAMENTO > VER',
        '/tributario/contacorrente/bloqueioparcelamento/visualizar.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/contacorrente/bloqueioparcelamento/lista.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'BLOQUEIO DE PARCELAMENTO',
        '/tributario/contacorrente/bloqueioparcelamento/lista.xhtml',
        (select id from menu where trim(label) = 'PARCELAMENTO DE DÉBITOS' and caminho is null), 0);
