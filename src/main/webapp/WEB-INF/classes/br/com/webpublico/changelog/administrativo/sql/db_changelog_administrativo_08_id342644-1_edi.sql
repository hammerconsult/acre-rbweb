update licitacao set modalidadelicitacao = 'CONCORRENCIA' where modalidadelicitacao = 'CONCORRENCIA_PUBLICA';
update amparolegal set modalidadelicitacao = 'CONCORRENCIA' where modalidadelicitacao = 'CONCORRENCIA_PUBLICA';
update contratosvigente set modalidade = 'CONCORRENCIA' where modalidade = 'CONCORRENCIA_PUBLICA';
update REGISTROSOLMATEXT set MODALIDADECARONA = 'CONCORRENCIA' where MODALIDADECARONA = 'CONCORRENCIA_PUBLICA';
update solicitacaomaterial set modalidadelicitacao = 'CONCORRENCIA' where modalidadelicitacao = 'CONCORRENCIA_PUBLICA';
update exclusaolicitacao set modalidadelicitacao = 'CONCORRENCIA' where modalidadelicitacao = 'CONCORRENCIA_PUBLICA';
update empenho set modalidadelicitacao = 'CONCORRENCIA' where modalidadelicitacao = 'CONCORRENCIA_PUBLICA';

update SOLICITACAOMATERIAL set amparolegal_id = null;
delete from amparolegal;

INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 0, 'Lei 8.666/1993', null, null, to_date('21/06/1993', 'dd/MM/yyyy'), to_date('28/02/2023', 'dd/MM/yyyy'), null, 'LEI_8666');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 1, 'Lei 14.133/2021, Art. 28, I', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'PREGAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 2, 'Lei 14.133/2021, Art. 28, II', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'CONCORRENCIA', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 3, 'Lei 14.133/2021, Art. 28, III', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'CONCURSO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 4, 'Lei 14.133/2021, Art. 28, IV', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'LEILAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 5, 'Lei 14.133/2021, Art. 28, V', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DIALOGO_COMPETITIVO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 6, 'Lei 14.133/2021, Art. 74, I', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 7, 'Lei 14.133/2021, Art. 74, II', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 8, 'Lei 14.133/2021, Art. 74, III, a', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 9, 'Lei 14.133/2021, Art. 74, III, b', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 10, 'Lei 14.133/2021, Art. 74, III, c', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 11, 'Lei 14.133/2021, Art. 74, III, d', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 12, 'Lei 14.133/2021, Art. 74, III, e', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 13, 'Lei 14.133/2021, Art. 74, III, f', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 14, 'Lei 14.133/2021, Art. 74, III, g', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 15, 'Lei 14.133/2021, Art. 74, III, h', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 16, 'Lei 14.133/2021, Art. 74, IV', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 17, 'Lei 14.133/2021, Art. 74, V', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 18, 'Lei 14.133/2021, Art. 75, I', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 19, 'Lei 14.133/2021, Art. 75, II', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 20, 'Lei 14.133/2021, Art. 75, III, a', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 21, 'Lei 14.133/2021, Art. 75, III, b', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 21, 'Lei 14.133/2021, Art. 75, IV, a', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 23, 'Lei 14.133/2021, Art. 75, IV, b', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 24, 'Lei 14.133/2021, Art. 75, IV, c', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 25, 'Lei 14.133/2021, Art. 75, IV, d', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 26, 'Lei 14.133/2021, Art. 75, IV, e', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 27, 'Lei 14.133/2021, Art. 75, IV, f', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 28, 'Lei 14.133/2021, Art. 75, IV, g', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 29, 'Lei 14.133/2021, Art. 75, IV, h', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 30, 'Lei 14.133/2021, Art. 75, IV, i', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 31, 'Lei 14.133/2021, Art. 75, IV, j', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 32, 'Lei 14.133/2021, Art. 75, IV, k', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 33, 'Lei 14.133/2021, Art. 75, IV, l', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 34, 'Lei 14.133/2021, Art. 75, IV, m', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 35, 'Lei 14.133/2021, Art. 75, V', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 36, 'Lei 14.133/2021, Art. 75, VI', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 37, 'Lei 14.133/2021, Art. 75, VII', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 38, 'Lei 14.133/2021, Art. 75, VIII', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 39, 'Lei 14.133/2021, Art. 75, IX', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 40, 'Lei 14.133/2021, Art. 75, X', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 41, 'Lei 14.133/2021, Art. 75, XI', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 42, 'Lei 14.133/2021, Art. 75, XII', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 43, 'Lei 14.133/2021, Art. 75, XIII', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 44, 'Lei 14.133/2021, Art. 75, XIV', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 45, 'Lei 14.133/2021, Art. 75, XV', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 46, 'Lei 14.133/2021, Art. 75, XVI', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'DISPENSA_LICITACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 47, 'Lei 14.133/2021, Art. 78, I', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'CREDENCIAMENTO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 48, 'Lei 14.133/2021, Art. 78, II', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'PRE_QUALIFICACAO', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 49, 'Lei 14.133/2021, Art. 78, III', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'MANIFESTACAO_INTERESSE', 'LEI_14133');
INSERT INTO AMPAROLEGAL VALUES (HIBERNATE_SEQUENCE.nextval, 50, 'Lei 14.133/2021, Art. 74, caput', null, null, to_date('01/03/2023', 'dd/MM/yyyy'), null, 'INEXIGIBILIDADE', 'LEI_14133');

update SOLICITACAOMATERIAL set amparolegal_id = (select id from amparolegal where codigo = 0);



