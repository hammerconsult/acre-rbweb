insert into CONFIGCUNIDADEDOCBLOQ(ID, CONFIGURACAOCONTABIL_ID, UNIDADEORGANIZACIONAL_ID, BLOQUEADO)
select hibernate_sequence.nextval as ID,
       (SELECT ID FROM CONFIGURACAOCONTABIL where ROWNUM = 1) as CONFIGURACAOCONTABIL_ID,
       ho.subordinada_id as UNIDADEORGANIZACIONAL_ID,
       0 as BLOQUEADO
from hierarquiaorganizacional ho
where ho.tipohierarquiaorganizacional = 'ORCAMENTARIA'
  and ho.indicedono = 3
  and sysdate between ho.iniciovigencia and coalesce(ho.fimvigencia, sysdate)
