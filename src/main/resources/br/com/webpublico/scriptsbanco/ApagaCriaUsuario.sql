--Criar usuario com a tablespace ja existente 23/02/2011.

Drop user webpublico cascade;
commit;

create user webpublico identified by senha10
default tablespace webpublico
temporary tablespace temp;

grant dba to webpublico;
GRANT IMP_FULL_DATABASE to webpublico;
