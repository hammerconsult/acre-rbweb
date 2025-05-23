insert into movimentoitemcontrato (ID, ITEMCONTRATO_ID, DATAMOVIMENTO, IDMOVIMENTO, IDORIGEM, ORIGEM, TIPO, OPERACAO,
                                   QUANTIDADE, VALORUNITARIO, VALORTOTAL, SUBTIPO, INDICE)
select HIBERNATE_SEQUENCE.nextval,
       ic.id,
       rc.RESCINDIDOEM,
       c.id,
       c.id,
       'CONTRATO',
       'RESCISAO',
       'PRE_EXECUCAO',
       ic.QUANTIDADETOTALCONTRATO,
       ic.VALORUNITARIO,
       ic.VALORTOTAL,
       'EXECUCAO',
       (select max(mov.indice) + 1 from MOVIMENTOITEMCONTRATO mov where mov.ITEMCONTRATO_ID = ic.id)
from contrato c
         inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
         inner join itemcontrato ic on ic.CONTRATO_ID = c.id;


insert into SALDOITEMCONTRATO (ID, ITEMCONTRATO_ID, IDORIGEM, DATASALDO, ORIGEM, OPERACAO, SALDOQUANTIDADE,
                               VALORUNITARIO, SALDOVALOR, SUBTIPO)
select HIBERNATE_SEQUENCE.nextval,
       ic.id,
       c.id,
       rc.RESCINDIDOEM,
       'CONTRATO',
       'PRE_EXECUCAO',
       0,
       0,
       0,
       'EXECUCAO'
from contrato c
         inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
         inner join itemcontrato ic on ic.CONTRATO_ID = c.id;


insert into movimentoitemcontrato (ID, ITEMCONTRATO_ID, DATAMOVIMENTO, IDMOVIMENTO, IDORIGEM, ORIGEM, TIPO, OPERACAO,
                                   QUANTIDADE, VALORUNITARIO, VALORTOTAL, SUBTIPO, INDICE)
select HIBERNATE_SEQUENCE.nextval,
       ic.id,
       rc.RESCINDIDOEM,
       c.id,
       c.id,
       'CONTRATO',
       'RESCISAO',
       'PRE_EXECUCAO',
       ic.QUANTIDADETOTALCONTRATO,
       ic.VALORUNITARIO,
       ic.VALORTOTAL,
       'VARIACAO',
       (select max(mov.indice) + 1 from MOVIMENTOITEMCONTRATO mov where mov.ITEMCONTRATO_ID = ic.id)
from contrato c
         inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
         inner join itemcontrato ic on ic.CONTRATO_ID = c.id;


insert into SALDOITEMCONTRATO (ID, ITEMCONTRATO_ID, IDORIGEM, DATASALDO, ORIGEM, OPERACAO, SALDOQUANTIDADE,
                               VALORUNITARIO, SALDOVALOR, SUBTIPO)
select HIBERNATE_SEQUENCE.nextval,
       ic.id,
       c.id,
       rc.RESCINDIDOEM,
       'CONTRATO',
       'PRE_EXECUCAO',
       0,
       0,
       0,
       'VARIACAO'
from contrato c
         inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
         inner join itemcontrato ic on ic.CONTRATO_ID = c.id
where c.id = 11166911079;

update itemcontrato
set valortotalrescisao = valortotal,
    quantidaderescisao = QUANTIDADETOTALCONTRATO
where id in (select ic.id
             from contrato c
                      inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
                      inner join itemcontrato ic on ic.CONTRATO_ID = c.id);


insert into PROXIMOVENCEDORLICITEM (ID, ITEMPREGAO_ID, PROXIMOVENCEDORLICITACAO_ID, VALORLANCEATUAL, VALORPROXIMOLANCE,
                                    PROXIMOVENCEDOR_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, 11032334418, 11097371500, 16, 23.53, 11094102091);
