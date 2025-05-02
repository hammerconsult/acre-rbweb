--ANEXO I
insert into anexolei1232006 (id, descricao, vigenteate)
select hibernate_sequence.nextval, 'Anexo I', to_date('31/12/2024', 'dd/MM/yyyy')
from dual
where not exists (select 1 from anexolei1232006 a where a.descricao = 'Anexo I');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       180000.00,
       4.00,
       0.00,
       5.50,
       3.50,
       12.74,
       41.50,
       0.00,
       '1ª Faixa',
       0.00,
       34.00,
       2.76
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '1ª Faixa');
INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)

select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       360000.00,
       7.30,
       5940.00,
       5.50,
       3.50,
       12.74,
       41.50,
       0.00,
       '2ª Faixa',
       0.00,
       34.00,
       2.76
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '2ª Faixa');
INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)

select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       720000.00,
       9.50,
       13860.00,
       5.50,
       3.50,
       12.74,
       42.00,
       0.00,
       '3ª Faixa',
       0.00,
       33.50,
       2.76
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '3ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       1800000.00,
       10.70,
       22500.00,
       5.50,
       3.50,
       12.74,
       42.00,
       0.00,
       '4ª Faixa',
       0.00,
       33.50,
       2.76
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '4ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       3600000.00,
       14.30,
       87300.00,
       5.50,
       3.50,
       12.74,
       42.00,
       0.00,
       '5ª Faixa',
       0.00,
       33.50,
       2.76
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '5ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I'),
       4800000.00,
       19.00,
       378000.00,
       13.50,
       10.00,
       28.27,
       42.10,
       0.00,
       '6ª Faixa',
       0.00,
       0.00,
       6.13
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo I')
                    and af.FAIXA = '6ª Faixa');

--ANEXO II
insert into anexolei1232006 (id, descricao, vigenteate)
select hibernate_sequence.nextval, 'Anexo II', to_date('31/12/2024', 'dd/MM/yyyy')
from dual
where not exists (select 1 from anexolei1232006 a where a.descricao = 'Anexo II');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       180000.00,
       4.50,
       0.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '1ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '1ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       360000.00,
       7.80,
       5940.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '2ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '2ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       720000.00,
       10.00,
       13860.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '3ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '3ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       1800000.00,
       11.20,
       22500.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '4ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '4ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       3600000.00,
       14.70,
       85500.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '5ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '5ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II'),
       4800000.00,
       30.00,
       720000.00,
       5.50,
       3.50,
       11.51,
       37.50,
       0.00,
       '6ª Faixa',
       7.50,
       32.00,
       2.49
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo II')
                    and af.FAIXA = '6ª Faixa');

--ANEXO III
insert into anexolei1232006 (id, descricao, vigenteate)
select hibernate_sequence.nextval, 'Anexo III', to_date('31/12/2024', 'dd/MM/yyyy')
from dual
where not exists (select 1 from anexolei1232006 a where a.descricao = 'Anexo III');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       180000.00,
       6.00,
       0.00,
       4.00,
       3.50,
       12.82,
       43.40,
       33.50,
       '1ª Faixa',
       0.00,
       0.00,
       2.78
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '1ª Faixa');
INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       360000.00,
       11.20,
       9360.00,
       4.00,
       3.50,
       14.05,
       43.40,
       32.00,
       '2ª Faixa',
       0.00,
       0.00,
       3.05
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '2ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       720000.00,
       13.50,
       17640.00,
       4.00,
       3.50,
       13.64,
       43.40,
       32.50,
       '3ª Faixa',
       0.00,
       0.00,
       2.96
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '3ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       1800000.00,
       16.00,
       35640.00,
       4.00,
       3.50,
       13.64,
       43.40,
       32.50,
       '4ª Faixa',
       0.00,
       0.00,
       2.96
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '4ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       3600000.00,
       21.00,
       125640.00,
       4.00,
       3.50,
       12.82,
       43.40,
       33.50,
       '5ª Faixa',
       0.00,
       0.00,
       2.78
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '5ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III'),
       4800000.00,
       33.00,
       648000.00,
       35.00,
       15.00,
       16.00,
       30.50,
       0.00,
       '6ª Faixa',
       0.00,
       0.00,
       3.47
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo III')
                    and af.FAIXA = '6ª Faixa');

--ANEXO IV
insert into anexolei1232006 (id, descricao, vigenteate)
select hibernate_sequence.nextval, 'Anexo IV', to_date('31/12/2024', 'dd/MM/yyyy')
from dual
where not exists (select 1 from anexolei1232006 a where a.descricao = 'Anexo IV');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       180000.00,
       4.50,
       0.00,
       18.80,
       15.20,
       17.67,
       0.00,
       44.50,
       '1ª Faixa',
       0.00,
       0.00,
       3.83
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '1ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       360000.00,
       9.00,
       8100.00,
       19.80,
       15.20,
       20.55,
       0.00,
       40.00,
       '2ª Faixa',
       0.00,
       0.00,
       4.45
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '2ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       720000.00,
       10.20,
       12420.00,
       20.80,
       15.20,
       19.73,
       0.00,
       40.00,
       '3ª Faixa',
       0.00,
       0.00,
       4.27
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '3ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       1800000.00,
       14.00,
       39780.00,
       17.80,
       19.20,
       18.90,
       0.00,
       40.00,
       '4ª Faixa',
       0.00,
       0.00,
       4.10
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '4ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       3600000.00,
       22.00,
       183780.00,
       18.80,
       19.20,
       18.08,
       0.00,
       40.00,
       '5ª Faixa',
       0.00,
       0.00,
       3.92
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '5ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV'),
       4800000.00,
       33.00,
       828000.00,
       53.50,
       21.50,
       20.55,
       0.00,
       0.00,
       '6ª Faixa',
       0.00,
       0.00,
       4.45
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo IV')
                    and af.FAIXA = '6ª Faixa');

--ANEXO V
insert into anexolei1232006 (id, descricao, vigenteate)
select hibernate_sequence.nextval, 'Anexo V', to_date('31/12/2024', 'dd/MM/yyyy')
from dual
where not exists (select 1 from anexolei1232006 a where a.descricao = 'Anexo V');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       180000.00,
       15.50,
       0.00,
       25.00,
       15.00,
       14.10,
       28.85,
       14.00,
       '1ª Faixa',
       0.00,
       0.00,
       3.05
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '1ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       360000.00,
       18.00,
       4500.00,
       23.00,
       15.00,
       14.10,
       27.85,
       17.00,
       '2ª Faixa',
       0.00,
       0.00,
       3.05
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '2ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       720000.00,
       19.50,
       9900.00,
       24.00,
       15.00,
       14.92,
       23.85,
       19.00,
       '3ª Faixa',
       0.00,
       0.00,
       3.23
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '3ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       1800000.00,
       20.50,
       17100.00,
       21.00,
       15.00,
       15.74,
       23.85,
       21.00,
       '4ª Faixa',
       0.00,
       0.00,
       3.41
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '4ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       3600000.00,
       23.00,
       62100.00,
       23.00,
       12.50,
       14.10,
       23.85,
       23.50,
       '5ª Faixa',
       0.00,
       0.00,
       3.05
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '5ª Faixa');

INSERT INTO anexolei1232006faixa (id, anexolei1232006_id, valormaximo, aliquota, valordeduzir,
                                  irpj, csll, cofins, cpp, iss, faixa, ipi, icms, pispasep)
select HIBERNATE_SEQUENCE.nextval,
       (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V'),
       4800000.00,
       30.50,
       540000.00,
       35.00,
       15.50,
       16.44,
       29.50,
       0.00,
       '6ª Faixa',
       0.00,
       0.00,
       3.56
from dual
where not exists (select 1
                  from anexolei1232006faixa af
                  where af.ANEXOLEI1232006_ID = (select id from ANEXOLEI1232006 where DESCRICAO = 'Anexo V')
                    and af.FAIXA = '6ª Faixa');

update configuracaotributario
set anexoLei1232006_id = (select id from anexolei1232006 where descricao = 'Anexo III');
