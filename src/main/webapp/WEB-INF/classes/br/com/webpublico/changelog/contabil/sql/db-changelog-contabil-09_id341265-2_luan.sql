insert into MODELODOCTOOFICIAL (ID, TIPODOCTOOFICIAL_ID, VIGENCIAINICIAL, SEQUENCIA)
VALUES (HIBERNATE_SEQUENCE.nextval, (select tdo.id from TIPODOCTOOFICIAL tdo where tdo.CODIGO = 158 and tdo.MODULOTIPODOCTOOFICIAL = 'NOTA_RESTO_ESTORNO_EMPENHO'), sysdate, 1)
