insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '1 - Ocupação Construção',
       1,
       1,
       'DISCRETO',
       'ni_ocupacao_construcao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_ocupacao_construcao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao_construcao'),
       current_date,
       1,
       'Não Construidos'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao_construcao')
                    and valor = 'Não Construidos');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao_construcao'),
       current_date,
       2,
       'Construidos'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao_construcao')
                    and valor = 'Construidos');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '2 - Tipo de Padrão',
       1,
       2,
       'DISCRETO',
       'ni_tipo_padrao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_tipo_padrao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       1,
       'Apartamento/Econômico'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Apartamento/Econômico');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       2,
       'Templo/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Templo/');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       3,
       'Edificação Industrial/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Edificação Industrial/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       4,
       'Loja/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Loja/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       5,
       'Escola/Universidade/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Escola/Universidade/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       6,
       'Pavilhão/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Pavilhão/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       7,
       'Piscina/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Piscina/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       8,
       'Edificação Industrial/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Edificação Industrial/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       9,
       'Sala Comercial/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Sala Comercial/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       10,
       'Garagem/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Garagem/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       11,
       'Hotel/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Hotel/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       12,
       'Cinema/Teatro/Clube/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Cinema/Teatro/Clube/');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       13,
       'Casa/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       14,
       'Sala Comercial/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Sala Comercial/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       15,
       'Cinema/Teatro/Clube/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Cinema/Teatro/Clube/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       16,
       'Casa/Luxo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Luxo');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       17,
       'Pavilhão/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Pavilhão/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       18,
       'Pavilhão/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Pavilhão/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       19,
       'Casa/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       20,
       'Casa/Econômico'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Econômico');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       21,
       '/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = '/');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       22,
       'Loja/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Loja/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       23,
       'Edificação Industrial/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Edificação Industrial/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       24,
       'Apartamento/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Apartamento/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       25,
       'Apartamento/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Apartamento/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       26,
       'Apartamento/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Apartamento/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       27,
       'Pavilhão/Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Pavilhão/Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       28,
       'Hotel/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Hotel/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       29,
       'Loja/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Loja/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       30,
       'Sala Comercial/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Sala Comercial/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       31,
       'Sala Comercial/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Sala Comercial/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       32,
       'Galpão/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Galpão/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       33,
       'Apartamento/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Apartamento/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       34,
       'Edificação Industrial/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Edificação Industrial/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       35,
       'Telheiro/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Telheiro/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       36,
       'Templo/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Templo/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       37,
       'Telheiro/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Telheiro/');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       38,
       'Galpão/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Galpão/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       39,
       'Templo/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Templo/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       40,
       'Escola/Universidade/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Escola/Universidade/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       41,
       'Escola/Universidade/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Escola/Universidade/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       42,
       'Templo/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Templo/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       43,
       'Casa/Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Superior');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       44,
       'Galpão/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Galpão/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       45,
       'Casa/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Casa/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       46,
       'Loja/Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Loja/Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       47,
       'Posto de Combustível/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Posto de Combustível/');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       48,
       'Telheiro/Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Telheiro/Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipo_padrao'),
       current_date,
       49,
       'Hospital/'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipo_padrao')
                    and valor = 'Hospital/');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '3 - Energia',
       1,
       3,
       'DISCRETO',
       'ni_energia',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_energia');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_energia'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_energia')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_energia'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_energia')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '4 - Telefone',
       1,
       4,
       'DISCRETO',
       'ni_telefone',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_telefone');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_telefone'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_telefone')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_telefone'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_telefone')
                    and valor = 'Não');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '5 - Idade',
       1,
       5,
       'DISCRETO',
       'ni_idade',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_idade');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_idade'),
       current_date,
       1,
       'ACIMA DE 50 ANOS'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_idade')
                    and valor = 'ACIMA DE 50 ANOS');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_idade'),
       current_date,
       2,
       'DE 11 A 30 ANOS'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_idade')
                    and valor = 'DE 11 A 30 ANOS');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_idade'),
       current_date,
       3,
       'DE 31 A 50 ANOS'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_idade')
                    and valor = 'DE 31 A 50 ANOS');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_idade'),
       current_date,
       4,
       ' DE 11 A 30 ANOS'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_idade')
                    and valor = ' DE 11 A 30 ANOS');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_idade'),
       current_date,
       5,
       'ATÉ 10 ANOS'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_idade')
                    and valor = 'ATÉ 10 ANOS');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '6 - Padrão',
       1,
       6,
       'DISCRETO',
       'ni_padrao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_padrao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_padrao'), current_date, 1, 'Luxo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Luxo');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_padrao'), current_date, 2, 'Fino'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Fino');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_padrao'), current_date, 3, 'Médio'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Médio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_padrao'),
       current_date,
       4,
       'Simples'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Simples');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_padrao'),
       current_date,
       5,
       'Econômico'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Econômico');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_padrao'),
       current_date,
       6,
       'Superior'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_padrao')
                    and valor = 'Superior');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '7 - Galeria',
       1,
       7,
       'DISCRETO',
       'ni_galeria',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_galeria');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_galeria'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_galeria')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_galeria'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_galeria')
                    and valor = 'Não');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '8 - Iluminação',
       1,
       8,
       'DISCRETO',
       'ni_iluminacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_iluminacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_iluminacao'),
       current_date,
       1,
       'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_iluminacao')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_iluminacao'),
       current_date,
       2,
       'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_iluminacao')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '9 - Utilização',
       1,
       9,
       'DISCRETO',
       'ni_utilizacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_utilizacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       1,
       'Sem Uso'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Sem Uso');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       2,
       'Religioso'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Religioso');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       3,
       'Industrial'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Industrial');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       4,
       'Hospitalar'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Hospitalar');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       5,
       'Institucional/ONG'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Institucional/ONG');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       6,
       'Residencial'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Residencial');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       7,
       'Misto'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Misto');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       8,
       'Serviço público'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Serviço público');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_utilizacao'),
       current_date,
       9,
       'Comercial/Prestação de Serviços'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_utilizacao')
                    and valor = 'Comercial/Prestação de Serviços');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '10 - Tipologia',
       1,
       10,
       'DISCRETO',
       'ni_tipologia',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_tipologia');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       1,
       'Telheiro'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Telheiro');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       2,
       'Edificação Industrial'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Edificação Industrial');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       3,
       'Apartamento'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Apartamento');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       4,
       'Templo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Templo');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       5,
       'Escola/Universidade'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Escola/Universidade');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       6,
       'Posto de Combustível'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Posto de Combustível');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       7,
       'Piscina'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Piscina');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       8,
       'Garagem'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Garagem');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       9,
       'Casa'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Casa');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       10,
       'Sala Comercial'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Sala Comercial');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       11,
       'Cinema/Teatro/Clube'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Cinema/Teatro/Clube');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       12,
       'Galpão'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Galpão');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       13,
       'Hospital'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Hospital');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       14,
       'Pavilhão'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Pavilhão');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       15,
       'Hotel'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Hotel');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_tipologia'),
       current_date,
       16,
       'Loja'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_tipologia')
                    and valor = 'Loja');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'CONSTRUCAO',
       'COMBO',
       current_date,
       '11 - Conservação',
       1,
       11,
       'DISCRETO',
       'ni_conservacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_conservacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_conservacao'),
       current_date,
       1,
       'Ruim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_conservacao')
                    and valor = 'Ruim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_conservacao'),
       current_date,
       2,
       'Bom'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_conservacao')
                    and valor = 'Bom');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_conservacao'),
       current_date,
       3,
       'Péssimo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_conservacao')
                    and valor = 'Péssimo');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_conservacao'),
       current_date,
       4,
       'Regular'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_conservacao')
                    and valor = 'Regular');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_conservacao'),
       current_date,
       5,
       'Ótimo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_conservacao')
                    and valor = 'Ótimo');
