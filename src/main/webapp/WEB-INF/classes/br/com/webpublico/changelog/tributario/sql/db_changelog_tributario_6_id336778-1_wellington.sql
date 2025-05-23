insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > DÍVIDA ATIVA > CERTIDÃO > EXPORTAÇÃO',
        '/tributario/dividaativa/certidaodividaativa/exportacaocda.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gruporecurso.id, recursosistema.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'ADMINISTRADOR TRIBUTÁRIO'
  and recursosistema.caminho = '/tributario/dividaativa/certidaodividaativa/exportacaocda.xhtml'
  and not exists(select 1 from gruporecursosistema s
                 where s.gruporecurso_id = gruporecurso.id
                   and s.recursosistema_id = recursosistema.id);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'EXPORTAÇÃO DE CDA',
        '/tributario/dividaativa/certidaodividaativa/exportacaocda.xhtml',
        (select id from menu where label = 'CERTIDÃO' and caminho is null), 0);
