insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > IPTU > ARQUIVO CEDO > LISTA',
       '/tributario/iptu/arquivocedo/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/arquivocedo/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > IPTU > ARQUIVO CEDO > EDITA',
       '/tributario/iptu/arquivocedo/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/arquivocedo/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > IPTU > ARQUIVO CEDO > VISUALIZAR',
       '/tributario/iptu/arquivocedo/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/arquivocedo/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho like '/tributario/iptu/arquivocedo/%'
  and gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'ARQUIVO CEDO',
       '/tributario/iptu/arquivocedo/lista.xhtml', -126758001,
       (select max(ordem) + 10 from menu where pai_id = -126758001)
from dual
where not exists (select 1 from menu where caminho = '/tributario/iptu/arquivocedo/lista.xhtml');
