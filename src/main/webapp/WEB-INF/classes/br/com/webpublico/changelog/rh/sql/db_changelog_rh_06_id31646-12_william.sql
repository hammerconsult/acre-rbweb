insert into EXCLUSAOEVENTOESOCIAL
select hibernate_sequence.nextval, sysdate,sysdate, null, 'ARQUIVO_GERADO',s3000.id  from REGISTROEXCLUSAOS3000 s3000;

update REGISTROEXCLUSAOS3000 registro set EXCLUSAOEVENTOESOCIAL_ID = (select id from EXCLUSAOEVENTOESOCIAL where S30000TEMP_ID = registro.id);
