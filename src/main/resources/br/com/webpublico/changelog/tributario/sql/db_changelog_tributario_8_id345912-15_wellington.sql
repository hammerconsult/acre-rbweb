insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '1 - Transporte',
       1,
       1,
       'DISCRETO',
       'ni_transporte',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_transporte');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_transporte'),
       current_date,
       1,
       'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_transporte')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_transporte'),
       current_date,
       2,
       'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_transporte')
                    and valor = 'Não');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '2 - Esgoto',
       1,
       2,
       'DISCRETO',
       'ni_esgoto',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_esgoto');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_esgoto'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_esgoto')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_esgoto'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_esgoto')
                    and valor = 'Não');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '3 - Arborização',
       1,
       3,
       'DISCRETO',
       'ni_arborizacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_arborizacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_arborizacao'),
       current_date,
       1,
       'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_arborizacao')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_arborizacao'),
       current_date,
       2,
       'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_arborizacao')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '4 - Sarjeta',
       1,
       4,
       'DISCRETO',
       'ni_sarjeta',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_sarjeta');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_sarjeta'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_sarjeta')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_sarjeta'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_sarjeta')
                    and valor = 'Não');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '5 - Limpeza',
       1,
       5,
       'DISCRETO',
       'ni_limpeza',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_limpeza');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_limpeza'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_limpeza')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_limpeza'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_limpeza')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '6 - Patrimônio',
       1,
       6,
       'DISCRETO',
       'ni_patrimonio',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_patrimonio');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_patrimonio'),
       current_date,
       1,
       'Particular'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_patrimonio')
                    and valor = 'Particular');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '7 - Situação da Quadra',
       1,
       7,
       'DISCRETO',
       'ni_situacao_quadra',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_situacao_quadra');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_situacao_quadra'),
       current_date,
       1,
       'Esquina ou Mais de uma Frente'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_situacao_quadra')
                    and valor = 'Esquina ou Mais de uma Frente');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_situacao_quadra'),
       current_date,
       2,
       'Encravado'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_situacao_quadra')
                    and valor = 'Encravado');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_situacao_quadra'),
       current_date,
       3,
       'Meio de Quadra'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_situacao_quadra')
                    and valor = 'Meio de Quadra');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '8 - Topografia',
       1,
       8,
       'DISCRETO',
       'ni_topografia',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_topografia');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_topografia'),
       current_date,
       1,
       'Declive'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_topografia')
                    and valor = 'Declive');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_topografia'),
       current_date,
       2,
       'Irregular'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_topografia')
                    and valor = 'Irregular');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_topografia'),
       current_date,
       3,
       'Aclive'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_topografia')
                    and valor = 'Aclive');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_topografia'),
       current_date,
       4,
       'Plano'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_topografia')
                    and valor = 'Plano');

insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '9 - Ocupação',
       1,
       9,
       'DISCRETO',
       'ni_ocupacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_ocupacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao'),
       current_date,
       1,
       'Construído'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao')
                    and valor = 'Construído');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao'),
       current_date,
       2,
       'Não Edificado'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao')
                    and valor = 'Não Edificado');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao'),
       current_date,
       3,
       'Construção em Andamento'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao')
                    and valor = 'Construção em Andamento');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao'),
       current_date,
       4,
       'Construção Paralisada'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao')
                    and valor = 'Construção Paralisada');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_ocupacao'),
       current_date,
       5,
       'Ruínas'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_ocupacao')
                    and valor = 'Ruínas');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '10 - Pedologia',
       1,
       10,
       'DISCRETO',
       'ni_pedologia',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_pedologia');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pedologia'),
       current_date,
       1,
       'Normal'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pedologia')
                    and valor = 'Normal');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '11 - Pavimentação',
       1,
       11,
       'DISCRETO',
       'ni_pavimentacao',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_pavimentacao');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pavimentacao'),
       current_date,
       1,
       'Bloco Intertravado'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pavimentacao')
                    and valor = 'Bloco Intertravado');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pavimentacao'),
       current_date,
       2,
       'Asfalto'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pavimentacao')
                    and valor = 'Asfalto');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pavimentacao'),
       current_date,
       3,
       'Piçarra'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pavimentacao')
                    and valor = 'Piçarra');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pavimentacao'),
       current_date,
       4,
       'Sem'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pavimentacao')
                    and valor = 'Sem');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_pavimentacao'),
       current_date,
       5,
       'Tijolo'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_pavimentacao')
                    and valor = 'Tijolo');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '12 - Água',
       1,
       12,
       'DISCRETO',
       'ni_agua',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_agua');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_agua'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_agua')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_agua'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_agua')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '13 - Calçada',
       1,
       13,
       'DISCRETO',
       'ni_calcada',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_calcada');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_calcada'), current_date, 1, 'Sim'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_calcada')
                    and valor = 'Sim');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_calcada'), current_date, 2, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_calcada')
                    and valor = 'Não');


insert into atributo (id, classedoatributo, componentevisual, dataregistro, nome, obrigatorio, sequenciaapresentacao,
                      tipoatributo, identificacao, ativo, somenteleitura, grupoatributo_id)
select hibernate_sequence.nextval,
       'LOTE',
       'COMBO',
       current_date,
       '14 - Lixo',
       1,
       14,
       'DISCRETO',
       'ni_lixo',
       1,
       0,
       (select id from grupoatributo where codigo = 3)
from dual
where not exists (select 1 from atributo where identificacao = 'ni_lixo');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval,
       (select id from atributo where identificacao = 'ni_lixo'),
       current_date,
       1,
       'Alternada'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_lixo')
                    and valor = 'Alternada');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_lixo'), current_date, 2, 'Diário'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_lixo')
                    and valor = 'Diário');
insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
select hibernate_sequence.nextval, (select id from atributo where identificacao = 'ni_lixo'), current_date, 3, 'Não'
from dual
where not exists (select 1
                  from valorpossivel
                  where atributo_id = (select id from atributo where identificacao = 'ni_lixo')
                    and valor = 'Não');
