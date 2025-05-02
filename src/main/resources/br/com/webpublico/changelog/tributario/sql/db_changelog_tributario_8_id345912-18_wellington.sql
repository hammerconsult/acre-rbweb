insert into recursosistema (id, nome, caminho, cadastro, modulo)
select hibernate_sequence.nextval,
       'TRIBUTÁRIO > CADASTRO MUNICIPAL > CADASTRO IMOBILIÁRIO > IMPORTAÇÃO DE ATRIBUTO > LISTA',
       '/tributario/cadastromunicipal/cadastroimobiliario/importacaoatributo/edita.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists (select 1
                  from recursosistema
                  where caminho = '/tributario/cadastromunicipal/cadastroimobiliario/importacaoatributo/edita.xhtml');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
select gr.id, rs.id
from gruporecurso gr,
     recursosistema rs
where rs.caminho = '/tributario/cadastromunicipal/cadastroimobiliario/importacaoatributo/edita.xhtml'
  and gr.nome in ('ADMINISTRADOR TRIBUTÁRIO')
  and not exists (select 1
                  from gruporecursosistema grs
                  where grs.gruporecurso_id = gr.id
                    and grs.recursosistema_id = rs.id);

insert into menu (id, label, caminho, pai_id, ordem)
select hibernate_sequence.nextval,
       'IMPORTAÇÃO DE ATRIBUTOS GENÉRICOS',
       '/tributario/cadastromunicipal/cadastroimobiliario/importacaoatributo/edita.xhtml',
       (select id from menu where label = 'OPERAÇÕES COM IMÓVEL'),
       (select max(ordem) + 10 from menu where pai_id = (select id from menu where label = 'OPERAÇÕES COM IMÓVEL'))
from dual
where not exists (select 1 from menu where label = 'IMPORTAÇÃO DE ATRIBUTOS GENÉRICOS');
