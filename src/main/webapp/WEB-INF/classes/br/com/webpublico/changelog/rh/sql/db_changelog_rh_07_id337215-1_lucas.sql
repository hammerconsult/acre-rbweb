-- ADALCIDES DE AQUINO DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639091999
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639091999
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639091999
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'),
        639091999, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('05/04/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('04/05/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639091999
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/04/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/04/2021', 'dd/MM/yyyy'));

-- ALFREDO RENATO PENA BRAÑA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/09/2020', 'dd/MM/yyyy'), to_date('29/11/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638887381
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/11/1984', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/10/1989', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638887381
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/11/1984', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/10/1989', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638887381
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/11/1984', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/10/1989', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/09/2020', 'dd/MM/yyyy'), to_date('29/11/2020', 'dd/MM/yyyy'),
        638887381, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/12/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/12/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638887381
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('02/11/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('01/12/2020', 'dd/MM/yyyy'));

-- ALYNNE GLEICYANNE DE OLIVEIRA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639042027
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2015', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639042027
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2015', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639042027
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('03/02/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('02/02/2015', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'),
        639042027, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('05/04/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('04/05/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639042027
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/03/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/03/2021', 'dd/MM/yyyy'));

-- ANA PAULA SOUSA DE OLIVEIRA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2020', 'dd/MM/yyyy'), to_date('28/09/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638925455
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638925455
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
            where v.id = 638925455
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2020', 'dd/MM/yyyy'), to_date('28/09/2020', 'dd/MM/yyyy'),
        638925455, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/06/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/06/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638925455
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/06/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('15/07/2020', 'dd/MM/yyyy'));

-- ANTONIA DEYSE SEVERIANO CAMPOS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/05/2018', 'dd/MM/yyyy'), to_date('29/07/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638943255
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638943255
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
            where v.id = 638943255
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/05/2018', 'dd/MM/yyyy'), to_date('29/07/2018', 'dd/MM/yyyy'),
        638943255, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/04/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/04/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638943255
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/06/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/06/2018', 'dd/MM/yyyy'));

-- BENEDITA DOS ANJOS SILVA LIMA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985696
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/07/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/07/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985696
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/07/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/07/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638985696
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/07/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/07/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'),
        638985696, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/03/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/03/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638985696
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/05/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/06/2020', 'dd/MM/yyyy'));

-- BENJAMIN RODRIGUES DE LIMA FILHO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('10/12/2020', 'dd/MM/yyyy'), to_date('09/03/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638912140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/04/1988', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/03/1993', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638912140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/04/1988', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/03/1993', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638912140
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/04/1988', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/03/1993', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('10/12/2020', 'dd/MM/yyyy'), to_date('09/03/2021', 'dd/MM/yyyy'),
        638912140, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('10/11/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('09/12/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638912140
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/12/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/12/2020', 'dd/MM/yyyy'));

-- EDSON RIGAUD VIANA NETO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('20/01/2021', 'dd/MM/yyyy'), to_date('19/02/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040529
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy')
           and prog.INICIOVIGENCIA = to_date('20/01/2021', 'dd/MM/yyyy')),
        (select periodo.ID
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040529
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy')
           and periodo.STATUS = 'PARCIAL'));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639040529
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy')
              and periodo.STATUS = 'PARCIAL');

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('20/01/2021', 'dd/MM/yyyy'), to_date('19/02/2021', 'dd/MM/yyyy'),
        639040529, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('18/12/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('16/01/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639040529
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/01/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/01/2021', 'dd/MM/yyyy'));

-- ELIANE GOUVEIA CHAVES BERNARDO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2021', 'dd/MM/yyyy'), to_date('31/03/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092139
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092139
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639092139
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2021', 'dd/MM/yyyy'), to_date('31/03/2021', 'dd/MM/yyyy'),
        639092139, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/04/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639092139
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/03/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/03/2021', 'dd/MM/yyyy'));

-- ELIANE RODRIGUES DE MACEDO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2020', 'dd/MM/yyyy'), to_date('31/03/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639002916
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('07/02/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('06/02/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639002916
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('07/02/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('06/02/2017', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639002916
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('07/02/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('06/02/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2020', 'dd/MM/yyyy'), to_date('31/03/2020', 'dd/MM/yyyy'),
        639002916, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/04/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/04/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639002916
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/02/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('03/03/2020', 'dd/MM/yyyy'));

-- FRANCISCA FERREIRA DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092268
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092268
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639092268
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'),
        639092268, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('29/01/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('27/02/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639092268
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/04/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/04/2021', 'dd/MM/yyyy'));

-- FRANCISCO RAIMUNDO SILVA DO NASCIMENTO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2020', 'dd/MM/yyyy'), to_date('29/08/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638893021
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/06/1991', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/05/1996', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638893021
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/06/1991', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/05/1996', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638893021
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/06/1991', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/05/1996', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2020', 'dd/MM/yyyy'), to_date('29/08/2020', 'dd/MM/yyyy'),
        638893021, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/05/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/05/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638893021
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/06/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/06/2020', 'dd/MM/yyyy'));

-- GABRIELA MORAIS VAZ
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2020', 'dd/MM/yyyy'), to_date('01/08/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092244
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639092244
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639092244
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('26/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('25/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2020', 'dd/MM/yyyy'), to_date('01/08/2020', 'dd/MM/yyyy'),
        639092244, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/06/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/06/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639092244
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2020', 'dd/MM/yyyy'));

-- ISABELA BONA DE MATTOS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2020', 'dd/MM/yyyy'), to_date('29/05/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040352
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040352
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639040352
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/11/2014', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/11/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2020', 'dd/MM/yyyy'), to_date('29/05/2020', 'dd/MM/yyyy'),
        639040352, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('29/01/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('27/02/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639040352
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/02/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('26/11/2019', 'dd/MM/yyyy'));

-- IVANETE BARBOZA MENDONCA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2019', 'dd/MM/yyyy'), to_date('29/08/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638941623
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('07/04/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('06/04/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638941623
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('07/04/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('06/04/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638941623
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('07/04/2014', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('06/04/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2019', 'dd/MM/yyyy'), to_date('29/08/2019', 'dd/MM/yyyy'),
        638941623, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/05/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/05/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638941623
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/06/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/07/2019', 'dd/MM/yyyy'));

-- JOSE MARIA LIRA DE QUEIROZ
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('08/02/2015', 'dd/MM/yyyy'), to_date('08/05/2015', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638894293
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638894293
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
            where v.id = 638894293
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('08/02/2015', 'dd/MM/yyyy'), to_date('08/05/2015', 'dd/MM/yyyy'),
        638894293, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('07/01/2015', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('05/02/2015', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638894293
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/03/2015', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/03/2015', 'dd/MM/yyyy'));

-- KALLYNE DE ARAUJO BRAGA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('08/05/2020', 'dd/MM/yyyy'), to_date('05/08/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639060140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/12/2016', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639060140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/12/2016', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639060140
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/12/2011', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/12/2016', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('08/05/2020', 'dd/MM/yyyy'), to_date('05/08/2020', 'dd/MM/yyyy'),
        639060140, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('08/04/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('07/05/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639060140
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/05/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/06/2020', 'dd/MM/yyyy'));

-- LIDIA SANTOS PEGO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/12/2018', 'dd/MM/yyyy'), to_date('28/02/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066182
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066182
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639066182
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/12/2018', 'dd/MM/yyyy'), to_date('28/02/2019', 'dd/MM/yyyy'),
        639066182, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/11/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/11/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639066182
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('05/11/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('04/12/2018', 'dd/MM/yyyy'));

-- LUCILAINE APARECIDA DE SOUZA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/09/2019', 'dd/MM/yyyy'), to_date('30/11/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638968045
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638968045
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638968045
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/09/2019', 'dd/MM/yyyy'), to_date('30/11/2019', 'dd/MM/yyyy'),
        638968045, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/08/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/08/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638968045
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/10/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/10/2019', 'dd/MM/yyyy'));

-- MARCEL KARLOS BEZERRA PERES
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639042046
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639042046
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639042046
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'),
        639042046, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('05/04/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('04/05/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639042046
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/04/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/04/2021', 'dd/MM/yyyy'));

-- MARCIA CRISTINA CORDEIRO LOPES ALODIO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('21/12/2020', 'dd/MM/yyyy'), to_date('07/02/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638943424
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638943424
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
            where v.id = 638943424
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('21/12/2020', 'dd/MM/yyyy'), to_date('07/02/2021', 'dd/MM/yyyy'),
        638943424, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('08/02/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('09/03/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638943424
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/01/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/02/2021', 'dd/MM/yyyy'));

-- MARIA ANTONIA GOMES DE OLIVEIRA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/09/2020', 'dd/MM/yyyy'), to_date('29/11/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985242
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('11/08/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('10/08/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985242
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('11/08/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('10/08/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638985242
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('11/08/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('10/08/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/09/2020', 'dd/MM/yyyy'), to_date('29/11/2020', 'dd/MM/yyyy'),
        638985242, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('31/07/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('29/08/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638985242
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/09/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/09/2020', 'dd/MM/yyyy'));

-- MARIA DAS GRAÇAS GOMES DE OLIVEIRA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638976925
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('10/06/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('09/06/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638976925
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('10/06/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('09/06/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638976925
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('10/06/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('09/06/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/01/2021', 'dd/MM/yyyy'), to_date('03/04/2021', 'dd/MM/yyyy'),
        638976925, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('05/04/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('04/05/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638976925
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/04/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/04/2021', 'dd/MM/yyyy'));

-- MARIA DE FATIMA LIMA DOMINGOS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638941684
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('29/04/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('28/05/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638941684
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('29/04/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('28/05/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638941684
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('29/04/2014', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('28/05/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'),
        638941684, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/03/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/03/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638941684
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/05/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/06/2020', 'dd/MM/yyyy'));

-- MARTA ALVES MONTEIRO DE FREITAS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('06/04/2020', 'dd/MM/yyyy'), to_date('04/07/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066532
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066532
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639066532
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('06/04/2020', 'dd/MM/yyyy'), to_date('04/07/2020', 'dd/MM/yyyy'),
        639066532, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('06/07/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('04/08/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639066532
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2020', 'dd/MM/yyyy'));

-- RAILSON ANTONIO PONTES DE ASSIS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/10/2017', 'dd/MM/yyyy'), to_date('30/12/2017', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639059482
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('30/11/2016', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639059482
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('30/11/2016', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639059482
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/12/2011', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('30/11/2016', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/10/2017', 'dd/MM/yyyy'), to_date('30/12/2017', 'dd/MM/yyyy'),
        639059482, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/09/2017', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/09/2017', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639059482
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/12/2017', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/12/2017', 'dd/MM/yyyy'));

-- REGIANE MARIA LIRA MORAIS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('28/08/2016', 'dd/MM/yyyy'), to_date('25/11/2016', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638968612
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638968612
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
            where v.id = 638968612
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('28/08/2016', 'dd/MM/yyyy'), to_date('25/11/2016', 'dd/MM/yyyy'),
        638968612, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/07/2016', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/07/2016', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638968612
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/08/2016', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/08/2016', 'dd/MM/yyyy'));

-- RIZANE DE SOUZA ALENCAR
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638887141
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638887141
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
            where v.id = 638887141
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'),
        638887141, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('28/01/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('26/02/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638887141
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/02/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/03/2021', 'dd/MM/yyyy'));

-- SURILENE SILVA DOS SANTOS
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639034159
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639034159
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639034159
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('03/02/2015', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('02/02/2020', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/03/2021', 'dd/MM/yyyy'), to_date('29/05/2021', 'dd/MM/yyyy'),
        639034159, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('28/01/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('26/02/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639034159
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/02/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/03/2021', 'dd/MM/yyyy'));

-- VERA ALICE PEREIRA DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/05/2020', 'dd/MM/yyyy'), to_date('01/08/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638969299
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638969299
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638969299
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('04/05/2020', 'dd/MM/yyyy'), to_date('01/08/2020', 'dd/MM/yyyy'),
        638969299, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/08/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/09/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638969299
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2020', 'dd/MM/yyyy'));

-- WANDA PEREIRA DE SOUZA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2020', 'dd/MM/yyyy'), to_date('29/08/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985197
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('18/08/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/01/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985197
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('18/08/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/01/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638985197
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('18/08/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/01/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/06/2020', 'dd/MM/yyyy'), to_date('29/08/2020', 'dd/MM/yyyy'),
        638985197, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/05/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/05/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638985197
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2020', 'dd/MM/yyyy'));

-- JONISON DA SILVA SOUZA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639035732
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/01/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/01/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639035732
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/01/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/01/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639035732
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/01/2014', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/01/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/04/2020', 'dd/MM/yyyy'), to_date('29/06/2020', 'dd/MM/yyyy'),
        639035732, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/03/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/03/2020', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639035732
              and SITUACAOFUNCIONAL_ID = 6189980
              and sit.INICIOVIGENCIA = to_date('08/05/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('17/05/2020', 'dd/MM/yyyy'));

-- MARIA DO CARMO ALVES RIBEIRO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('09/12/2019', 'dd/MM/yyyy'), to_date('01/03/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638915607
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638915607
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638915607
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('09/12/2019', 'dd/MM/yyyy'), to_date('01/03/2020', 'dd/MM/yyyy'),
        638915607, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/11/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/11/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638915607
              and SITUACAOFUNCIONAL_ID = 6189980
              and sit.INICIOVIGENCIA = to_date('11/09/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('09/12/2019', 'dd/MM/yyyy'));

-- NEUMA BEZERRA JUSTINO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID,
                                   PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2020', 'dd/MM/yyyy'), to_date('31/03/2020', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('10/10/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('09/10/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985140
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('10/10/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('09/10/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638985140
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('10/10/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('09/10/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2020', 'dd/MM/yyyy'), to_date('31/03/2020', 'dd/MM/yyyy'),
        638985140, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/12/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/12/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638985140
              and SITUACAOFUNCIONAL_ID = 6189980
              and sit.INICIOVIGENCIA = to_date('13/01/2020', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA is null);
