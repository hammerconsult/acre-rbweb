insert into requisicaocompraexecucao (id, REQUISICAOCOMPRA_ID, EXECUCAOCONTRATO_ID)
select HIBERNATE_SEQUENCE.nextval as id,
       id                         as req,
       EXECUCAOCONTRATO_ID        as execucao
from REQUISICAODECOMPRA
where EXECUCAOCONTRATO_ID is not null;
