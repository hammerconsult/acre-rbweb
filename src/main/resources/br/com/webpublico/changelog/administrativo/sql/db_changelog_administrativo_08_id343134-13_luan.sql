insert into MODELODOCTOOFICIAL (ID, TIPODOCTOOFICIAL_ID, VIGENCIAINICIAL, SEQUENCIA)
VALUES (HIBERNATE_SEQUENCE.nextval, (select tdo.id
                                     from TIPODOCTOOFICIAL tdo
                                     where tdo.CODIGO = 168
                                       and tdo.MODULOTIPODOCTOOFICIAL = 'DFD'), sysdate, 1)
