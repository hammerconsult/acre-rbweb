-- 544607 - ISABELLY LEMOS BASTO DE OLIVEIRA ROSAS id - 638981421
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('11/05/2021', 'dd/MM/yyyy'), to_date('08/08/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 638981421
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638981421
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638981421
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date(' 11/05/2021', 'dd/MM/yyyy'), to_date('08/08/2021', 'dd/MM/yyyy'),
        638981421, 6189980);

delete SituacaoContratoFP where id =  10684805247;

update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('10/05/2021', 'dd/MM/yyyy')
where id = 10696548408;

update SituacaoContratoFP
set  INICIOVIGENCIA  = to_date('12/05/2021', 'dd/MM/yyyy')
where id = 10684805246;


-- 701565 - JONILSON FIRMINO DA SILVA id - 639026769
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2021', 'dd/MM/yyyy'), to_date('29/06/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 639026769
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639026769
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639026769
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));


insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date(' 01/04/2021', 'dd/MM/yyyy'), to_date('29/06/2021', 'dd/MM/yyyy'),
        639026769, 6189980);


update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('31/03/2021', 'dd/MM/yyyy')
where id = 10655170692;

delete SituacaoContratoFP where id =  10663030251;

update SituacaoContratoFP
set  INICIOVIGENCIA  = to_date('30/06/2021', 'dd/MM/yyyy')
where id = 10663030250;


update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639026769
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));


-- 543418 - LIDIA BRITTO DE SOUZA id - 638968074
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2021', 'dd/MM/yyyy'), to_date('29/08/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 638968074
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638968074
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('21/08/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('20/08/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638968074
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('21/08/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('20/08/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2021', 'dd/MM/yyyy'), to_date('29/08/2021', 'dd/MM/yyyy'),
        638968074, 6189980);


update SituacaoContratoFP
set  INICIOVIGENCIA  = to_date('30/08/2021', 'dd/MM/yyyy')
where id = 10652821421;

update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('02/05/2021', 'dd/MM/yyyy')
where id = 10666988082;


-- 546478 - WILLIAN ALFONSO FERREIRA FILGUEIRA id - 639004718

insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/02/2021', 'dd/MM/yyyy'), to_date('01/05/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 639004718
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('09/09/2010', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('08/09/2015', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639004718
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('09/09/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('08/09/2015', 'dd/MM/yyyy')));


update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639004718
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('09/09/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('08/09/2015', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/02/2021', 'dd/MM/yyyy'), to_date('01/05/2021', 'dd/MM/yyyy'),
        639004718, 6189980);


update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('31/01/2021', 'dd/MM/yyyy')
where id = 10615649935;

update SituacaoContratoFP
set  INICIOVIGENCIA  = to_date('02/05/2021', 'dd/MM/yyyy')
where id = 10615649934;


-- 2151 - FRANCISCO NUNES DA SILVA id - 638887558

insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/08/2021', 'dd/MM/yyyy'), to_date('29/10/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 638887558
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638887558
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')));


update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638887558
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('09/09/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('08/09/2015', 'dd/MM/yyyy'));


insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/08/2021', 'dd/MM/yyyy'), to_date('29/10/2021', 'dd/MM/yyyy'),
        638887558, 6189980);

update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('31/07/2021', 'dd/MM/yyyy')
where id = 899161087;

DELETE SituacaoContratoFP where id = 10699791066;
DELETE SituacaoContratoFP where id = 10699791065;

-- 544781 - HELLYETH SILVA DUARTE id - 638982954

insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('05/04/2021', 'dd/MM/yyyy'), to_date('03/07/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
 from ProgramacaoLicencaPremio prog
          inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
          inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
          inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
          inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
 where v.id = 638982954
   and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
   and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
   and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638982954
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')));


update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638982954
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));


insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('05/04/2021', 'dd/MM/yyyy'), to_date('03/07/2021', 'dd/MM/yyyy'),
        638982954, 6189980);

update SituacaoContratoFP
set  FINALVIGENCIA  = to_date('04/04/2021', 'dd/MM/yyyy')
where id = 10597668766;

update SituacaoContratoFP
set  INICIOVIGENCIA  = to_date('04/07/2021', 'dd/MM/yyyy')
where id = 10684803443;





