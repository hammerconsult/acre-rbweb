update CONFIGURACAOLICITACAO set DATAREFERENCIARESERVADOTACAO = to_date('01/01/2018', 'dd/MM/yyyy') where id = 683690586;
update CONFIGURACAOLICITACAO_AUD set DATAREFERENCIARESERVADOTACAO = to_date('01/01/2018', 'dd/MM/yyyy') where id = 683690586;
delete from CONFIGURACAORESERVADOTACAO where id = 683690588;

update SOLICITACAOMATERIAL set TIPONATUREZADOPROCEDIMENTO = 'NAO_APLICAVEL'
where TIPONATUREZADOPROCEDIMENTO is null;

update LICITACAO set NATUREZADOPROCEDIMENTO = 'NAO_APLICAVEL'
where NATUREZADOPROCEDIMENTO is null;

--PREGAO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'PREGAO', 'PRESENCIAL', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'PREGAO', 'ELETRONICO', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'PREGAO', 'PRESENCIAL_COM_REGISTRO_DE_PRECO', 'EXECUCAO_CONTRATO');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'PREGAO', 'ELETRONICO_COM_REGISTRO_DE_PRECO', 'EXECUCAO_CONTRATO');


--RDC
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'ABERTO', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'FECHADO', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'COMBINADO', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'ABERTA_COM_REGISTRO_DE_PRECO', 'EXECUCAO_CONTRATO');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'FECHADA_COM_REGISTRO_DE_PRECO', 'EXECUCAO_CONTRATO');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'RDC', 'COMBINADO_COM_REGISTRO_DE_PRECO', 'EXECUCAO_CONTRATO');


--CONCORRENCIA
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONCORRENCIA', 'NORMAL', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONCORRENCIA', 'CREDENCIAMENTO', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONCORRENCIA', 'REGISTRO_DE_PRECOS', 'EXECUCAO_CONTRATO');

--INEXIGIBILIDADE
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'INEXIGIBILIDADE', 'NORMAL', 'SOLICITACAO_COMPRA');

insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'INEXIGIBILIDADE', 'CREDENCIAMENTO', 'SOLICITACAO_COMPRA');

--CREDENCIAMENTO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CREDENCIAMENTO', 'CREDENCIAMENTO', 'EXECUCAO_CONTRATO');

--CONVITE
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONVITE', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--TOMADA_PRECO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'TOMADA_PRECO', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--CONCORRENCIA
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONCORRENCIA', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--CONCURSO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'CONCURSO', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--DISPENSA_LICITACAO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'DISPENSA_LICITACAO', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');


--MANIFESTACAO_INTERESSE
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'MANIFESTACAO_INTERESSE', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--PRE_QUALIFICACAO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'PRE_QUALIFICACAO', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

--DIALOGO_COMPETITIVO
insert into CONFIGURACAORESERVADOTACAO (ID, CONFIGURACAOLICITACAO_ID, MODALIDADELICITACAO, NATUREZAPROCEDIMENTO, TIPORESERVADOTACAO)
VALUES (HIBERNATE_SEQUENCE.nextval, 683690586, 'DIALOGO_COMPETITIVO', 'NAO_APLICAVEL', 'SOLICITACAO_COMPRA');

