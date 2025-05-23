insert into CONFIGURACAOEVENTOIPTU (ID, CODIGO, DESCRICAO, CRIADOEM, ATIVO, EXERCICIOINICIAL_ID, EXERCICIOFINAL_ID)
select HIBERNATE_SEQUENCE.nextval,
       (select max(codigo) + 1 from CONFIGURACAOEVENTOIPTU),
       'NOVO IPTU',
       null,
       1,
       (select id from exercicio where ano = 2024),
       (select id from exercicio where ano = 2024)
from dual
where not exists (select 1 from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU');

insert into EVENTOCONFIGURADOIPTU (ID, EVENTOCALCULO_ID, CONFIGURACAOEVENTOIPTU_ID, CRIADOEM)
select HIBERNATE_SEQUENCE.nextval,
       (select id from eventocalculo where IDENTIFICACAO = 'niIptu'),
       (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU'),
       null
from dual
where not exists (select 1
                  from EVENTOCONFIGURADOIPTU
                  where
                      CONFIGURACAOEVENTOIPTU_ID = (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU')
                    and EVENTOCALCULO_ID = (select id from eventocalculo where IDENTIFICACAO = 'niIptu'));

insert into EVENTOCONFIGURADOIPTU (ID, EVENTOCALCULO_ID, CONFIGURACAOEVENTOIPTU_ID, CRIADOEM)
select HIBERNATE_SEQUENCE.nextval,
       (select id from eventocalculo where IDENTIFICACAO = 'niTaxaColetaLixo'),
       (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU'),
       null
from dual
where not exists (select 1
                  from EVENTOCONFIGURADOIPTU
                  where
                      CONFIGURACAOEVENTOIPTU_ID = (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU')
                    and EVENTOCALCULO_ID = (select id from eventocalculo where IDENTIFICACAO = 'niTaxaColetaLixo'));

insert into EVENTOCONFIGURADOIPTU (ID, EVENTOCALCULO_ID, CONFIGURACAOEVENTOIPTU_ID, CRIADOEM)
select HIBERNATE_SEQUENCE.nextval,
       (select id from eventocalculo where IDENTIFICACAO = 'niIluminacaoPublica'),
       (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU'),
       null
from dual
where not exists (select 1
                  from EVENTOCONFIGURADOIPTU
                  where
                      CONFIGURACAOEVENTOIPTU_ID = (select id from CONFIGURACAOEVENTOIPTU where DESCRICAO = 'NOVO IPTU')
                    and EVENTOCALCULO_ID = (select id from eventocalculo where IDENTIFICACAO = 'niIluminacaoPublica'));
