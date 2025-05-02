insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > COBRANÇA ADMINISTRATIVA > PROCESSO DE COBRANÇA PELO SPC > LISTA',
       '/tributario/processocobrancaspc/lista.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/processocobrancaspc/lista.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > COBRANÇA ADMINISTRATIVA > PROCESSO DE COBRANÇA PELO SPC > EDITA',
       '/tributario/processocobrancaspc/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/processocobrancaspc/edita.xhtml');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > COBRANÇA ADMINISTRATIVA > PROCESSO DE COBRANÇA PELO SPC > VISUALIZAR',
       '/tributario/processocobrancaspc/visualizar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/processocobrancaspc/visualizar.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho like '/tributario/processocobrancaspc/%'
  and gr.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'PROCESSO DE COBRANÇA PELO SPC',
       '/tributario/processocobrancaspc/lista.xhtml', -126758315,
       (select max(ordem) + 10 from menu where pai_id = -126758315)
from dual
where not exists (select 1 from menu where caminho = '/tributario/processocobrancaspc/lista.xhtml');

