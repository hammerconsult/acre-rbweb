insert into ITEMREQUISICAOCOMPRAEXEC (id, ITEMREQUISICAOCOMPRA_ID, EXECUCAOCONTRATOITEM_ID, QUANTIDADE,
                                      VALORUNITARIO, VALORTOTAL)
select HIBERNATE_SEQUENCE.nextval  as id,
       irc.id                      as req,
       irc.EXECUCAOCONTRATOITEM_ID as item_contrato,
       irc.QUANTIDADE,
       irc.VALORUNITARIO,
       irc.VALORTOTAL
from ITEMREQUISICAODECOMPRA irc;
