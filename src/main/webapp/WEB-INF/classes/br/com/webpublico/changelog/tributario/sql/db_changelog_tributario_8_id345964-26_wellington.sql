insert into eventoconfiguradobci (ID, CONFIGURACAOTRIBUTARIO_ID, EVENTOCALCULO_ID, IDENTIFICACAO, INCIDENCIA, PATTERN,
                                  GRUPOATRIBUTO_ID)
select hibernate_sequence.nextval,
       (select id from configuracaotributario fetch first 1 row only),
       (select id from eventocalculo where identificacao = 'niValorVenalTerreno'),
       'VALOR_VENAL',
       'CADASTRO',
       '#,##0.00',
       (select id from GRUPOATRIBUTO where CODIGO = 3)
from dual
where not exists (select 1
                  from eventoconfiguradobci s
                  where configuracaotributario_id = (select id from configuracaotributario fetch first 1 row only)
                    and eventocalculo_id = (select id from eventocalculo where identificacao = 'niValorVenalTerreno'));

insert into eventoconfiguradobci (ID, CONFIGURACAOTRIBUTARIO_ID, EVENTOCALCULO_ID, IDENTIFICACAO, INCIDENCIA, PATTERN,
                                  GRUPOATRIBUTO_ID)
select hibernate_sequence.nextval,
       (select id from configuracaotributario fetch first 1 row only),
       (select id from eventocalculo where identificacao = 'niValorVenalConstrucoes'),
       'VALOR_VENAL',
       'CADASTRO',
       '#,##0.00',
       (select id from GRUPOATRIBUTO where CODIGO = 3)
from dual
where not exists (select 1
                  from eventoconfiguradobci s
                  where configuracaotributario_id = (select id from configuracaotributario fetch first 1 row only)
                    and eventocalculo_id =
                        (select id from eventocalculo where identificacao = 'niValorVenalConstrucoes'));

insert into eventoconfiguradobci (ID, CONFIGURACAOTRIBUTARIO_ID, EVENTOCALCULO_ID, IDENTIFICACAO, INCIDENCIA, PATTERN,
                                  GRUPOATRIBUTO_ID)
select hibernate_sequence.nextval,
       (select id from configuracaotributario fetch first 1 row only),
       (select id from eventocalculo where identificacao = 'niValorVenalImovel'),
       'VALOR_VENAL',
       'CADASTRO',
       '#,##0.00',
       (select id from GRUPOATRIBUTO where CODIGO = 3)
from dual
where not exists (select 1
                  from eventoconfiguradobci s
                  where configuracaotributario_id = (select id from configuracaotributario fetch first 1 row only)
                    and eventocalculo_id = (select id from eventocalculo where identificacao = 'niValorVenalImovel'));

insert into eventoconfiguradobci (ID, CONFIGURACAOTRIBUTARIO_ID, EVENTOCALCULO_ID, IDENTIFICACAO, INCIDENCIA, PATTERN,
                                  GRUPOATRIBUTO_ID)
select hibernate_sequence.nextval,
       (select id from configuracaotributario fetch first 1 row only),
       (select id from eventocalculo where identificacao = 'niAliquota'),
       'EVENTO_COMPLEMENTAR',
       'CADASTRO',
       '#,##0.00%',
       (select id from GRUPOATRIBUTO where CODIGO = 3)
from dual
where not exists (select 1
                  from eventoconfiguradobci s
                  where configuracaotributario_id = (select id from configuracaotributario fetch first 1 row only)
                    and eventocalculo_id = (select id from eventocalculo where identificacao = 'niAliquota'));
