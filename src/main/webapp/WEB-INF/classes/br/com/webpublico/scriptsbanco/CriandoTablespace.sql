--Criar tablespace e usuario 23/02/2011.


create tablespace webpublico
datafile '/home/oracle/app/oracle/oradata/orcl/webpublico.ora' SIZE 5M
AUTOEXTEND ON NEXT 5120K;

create user webpublico identified by senha10
default tablespace webpublico
temporary tablespace temp;

grant dba to webpublico;

--O script abaixo deve ser executado apenas na primeira vez para o BD Oracle.
--CREATE DIRECTORY dump AS '/home/oracle/dump';
--GRANT read, write ON DIRECTORY dump TO PUBLIC;
