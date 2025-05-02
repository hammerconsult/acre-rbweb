create table cancelamentonotatemp
(
    dms_id number,
    cancelamento_id number
);

insert into cancelamentonotatemp
SELECT DEC.ID AS dms_id,
       CANC.ID as cancelamento_id
FROM CANCDECLAPRESTACAOSERVICO CANC
         INNER JOIN DECLARACAOPRESTACAOSERVICO DEC ON DEC.CANCELAMENTO_ID = CANC.ID;

CREATE INDEX temp_cancelamento_dms ON cancelamentonotatemp(dms_id)
     COMPUTE STATISTICS;

CREATE INDEX temp_cancelamento_canc ON cancelamentonotatemp(cancelamento_id)
     COMPUTE STATISTICS;

update CANCDECLAPRESTACAOSERVICO canc
set canc.DECLARACAO_id = (select dms_id from cancelamentonotatemp temp where temp.cancelamento_id = canc.id)
where DECLARACAO_ID is null;
