insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > IPTU > RELATORIO ARQUIVO CEDO',
       '/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml'
  and gr.nome = 'ADMINISTRADOR_GERAL'
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval, 'RELATÓRIO ARQUIVO CEDO',
       '/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml', -126758001,
       (select max(ordem) + 10 from menu where pai_id = -126758001)
from dual
where not exists (select 1 from menu where caminho = '/tributario/iptu/arquivocedo/relatorioarquivocedo.xhtml');
