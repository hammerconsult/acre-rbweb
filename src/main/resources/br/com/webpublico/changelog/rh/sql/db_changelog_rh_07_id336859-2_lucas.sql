-- ANA MARIA RABELO DO NASCIMENTO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066966
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('17/09/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('16/09/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066966
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('17/09/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('16/09/2017', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639066966
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('17/09/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('16/09/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'),
        639066966, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/01/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/01/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639066966
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/12/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('01/01/2019', 'dd/MM/yyyy'));

-- ANTONIO CARLOS CONCEIÇÃO DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/02/2019', 'dd/MM/yyyy'), to_date('01/05/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639016190
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639016190
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
            where v.id = 639016190
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/02/2019', 'dd/MM/yyyy'), to_date('01/05/2019', 'dd/MM/yyyy'),
        639016190, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/02/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('02/03/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639016190
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/03/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/04/2019', 'dd/MM/yyyy'));

-- AQUILES SERGIO ALVES DA CRUZ
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2018', 'dd/MM/yyyy'), to_date('28/09/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639028015
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2008', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2013', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639028015
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2008', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2013', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639028015
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/02/2008', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/02/2013', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2018', 'dd/MM/yyyy'), to_date('28/09/2018', 'dd/MM/yyyy'),
        639028015, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/06/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/06/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639028015
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/08/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/08/2018', 'dd/MM/yyyy'));

-- CARLOS ALBERTO COELHO BIANCO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2019', 'dd/MM/yyyy'), to_date('31/03/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040557
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639040557
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639040557
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/11/2009', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/11/2014', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2019', 'dd/MM/yyyy'), to_date('31/03/2019', 'dd/MM/yyyy'),
        639040557, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/12/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('31/12/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639040557
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('07/01/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('05/02/2019', 'dd/MM/yyyy'));

-- CLEICILENE VIEIRA DA SILVA GUIMARAES
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2018', 'dd/MM/yyyy'), to_date('01/04/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639060189
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('30/11/2016', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639060189
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
            where v.id = 639060189
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/12/2011', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('30/11/2016', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2018', 'dd/MM/yyyy'), to_date('01/04/2018', 'dd/MM/yyyy'),
        639060189, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/04/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639060189
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/03/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/03/2018', 'dd/MM/yyyy'));

-- ERDEJANE CHAVES DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/02/2018', 'dd/MM/yyyy'), to_date('02/05/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639003072
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('12/12/2011', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('11/12/2016', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639003072
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
            where v.id = 639003072
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('12/12/2011', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('11/12/2016', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/02/2018', 'dd/MM/yyyy'), to_date('02/05/2018', 'dd/MM/yyyy'),
        639003072, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('02/04/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639003072
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/03/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/03/2018', 'dd/MM/yyyy'));

-- EDVARD DE ARAÚJO RODRIGUES
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2019', 'dd/MM/yyyy'), to_date('28/09/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639086423
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('06/05/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('05/05/2019', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639086423
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('06/05/2014', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('05/05/2019', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639086423
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('06/05/2014', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('05/05/2019', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2019', 'dd/MM/yyyy'), to_date('28/09/2019', 'dd/MM/yyyy'),
        639086423, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/07/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/07/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639086423
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/08/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/08/2019', 'dd/MM/yyyy'));

-- ELISSANDRO FRANKLIN DA ROCHA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639028827
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639028827
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639028827
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'),
        639028827, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/08/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/09/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639028827
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/09/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/10/2018', 'dd/MM/yyyy'));

-- EUNICE ARAUJO DE SOUZA BARBOSA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('06/05/2019', 'dd/MM/yyyy'), to_date('03/08/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639034437
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('08/07/2008', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('07/07/2013', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639034437
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('08/07/2008', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('07/07/2013', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639034437
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('08/07/2008', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('07/07/2013', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('06/05/2019', 'dd/MM/yyyy'), to_date('03/08/2019', 'dd/MM/yyyy'),
        639034437, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/04/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/04/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639034437
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('02/05/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('31/05/2019', 'dd/MM/yyyy'));

-- HELIANA SILVA SOUZA DANZICOURT
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('07/03/2021', 'dd/MM/yyyy'), to_date('04/06/2021', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638915174
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638915174
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
            where v.id = 638915174
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('07/03/2021', 'dd/MM/yyyy'), to_date('04/06/2021', 'dd/MM/yyyy'),
        638915174, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/03/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/03/2021', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638915174
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/04/2021', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/04/2021', 'dd/MM/yyyy'));

-- HUENDSON DE MELO QUEIROZ
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2019', 'dd/MM/yyyy'), to_date('28/09/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639029767
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639029767
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639029767
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/02/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/02/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/07/2019', 'dd/MM/yyyy'), to_date('28/09/2019', 'dd/MM/yyyy'),
        639029767, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('31/05/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('29/09/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639029767
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2019', 'dd/MM/yyyy'));

-- JANAINA BATISTA LIMA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639077267
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/04/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/04/2018', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639077267
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('15/04/2013', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('14/04/2018', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 639077267
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('15/04/2013', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('14/04/2018', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'),
        639077267, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/08/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/09/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639077267
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/09/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/10/2018', 'dd/MM/yyyy'));

-- MAIKON JONES SILVA DE MOURA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985342
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638985342
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
            where v.id = 638985342
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'),
        638985342, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/08/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/09/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638985342
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/09/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/10/2018', 'dd/MM/yyyy'));

-- MARIA NILZA RODRIGUES DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066892
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066892
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
            where v.id = 639066892
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/10/2018', 'dd/MM/yyyy'), to_date('29/12/2018', 'dd/MM/yyyy'),
        639066892, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('03/08/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/09/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639066892
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/09/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/10/2018', 'dd/MM/yyyy'));

-- NAYARA LIMA DA SILVA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/05/2018', 'dd/MM/yyyy'), to_date('30/07/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066401
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639066401
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
            where v.id = 639066401
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/05/2018', 'dd/MM/yyyy'), to_date('30/07/2018', 'dd/MM/yyyy'),
        639066401, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/08/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/08/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639066401
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('02/07/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('31/07/2018', 'dd/MM/yyyy'));

-- RAIMUNDA ALMEIDA CASTRO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/05/2019', 'dd/MM/yyyy'), to_date('29/07/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638984699
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638984699
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
            where v.id = 638984699
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/05/2019', 'dd/MM/yyyy'), to_date('29/07/2019', 'dd/MM/yyyy'),
        638984699, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/05/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/05/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638984699
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/06/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/07/2019', 'dd/MM/yyyy'));

-- JANE MARISE SILVA CARIOCA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('17/05/2013', 'dd/MM/yyyy'), to_date('14/08/2013', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638912127
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638912127
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
            where v.id = 638912127
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('17/05/2013', 'dd/MM/yyyy'), to_date('14/08/2013', 'dd/MM/yyyy'),
        638912127, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('17/04/2013', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('16/05/2013', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638912127
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('13/05/2013', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('11/06/2013', 'dd/MM/yyyy'));

-- ROSANA MADEIRA MAIA DE HOLANDA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2016', 'dd/MM/yyyy'), to_date('30/03/2016', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638928814
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('21/07/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('20/07/2015', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638928814
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('21/07/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('20/07/2015', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'CONCEDIDO',
    DATAATUALIZACAO = sysdate,
    SALDO           = 0
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638928814
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('21/07/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('20/07/2015', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2016', 'dd/MM/yyyy'), to_date('30/03/2016', 'dd/MM/yyyy'),
        638928814, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('01/12/2015', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('30/12/2015', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638928814
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('04/01/2016', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/02/2016', 'dd/MM/yyyy'));

-- SAMIA PORFIRIO DO CARMO
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2019', 'dd/MM/yyyy'), to_date('31/03/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639058955
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 639058955
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
            where v.id = 639058955
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('25/06/2012', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('24/06/2017', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('01/01/2019', 'dd/MM/yyyy'), to_date('31/03/2019', 'dd/MM/yyyy'),
        639058955, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('30/11/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('29/12/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 639058955
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('03/12/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('01/01/2019', 'dd/MM/yyyy'));

-- VERA LUCIA RIBEIRO DE OLIVEIRA FERREIRA
insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('03/05/2019', 'dd/MM/yyyy'), to_date('31/07/2019', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638892261
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638892261
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
            where v.id = 638892261
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2005', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2009', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('03/05/2019', 'dd/MM/yyyy'), to_date('31/07/2019', 'dd/MM/yyyy'),
        638892261, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('31/05/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('29/09/2019', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638892261
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/07/2019', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('30/07/2019', 'dd/MM/yyyy'));

-- EDNALDO TOMAS DA SILVA
insert into PROGRAMACAOLICENCAPREMIO(ID, DATACADASTRO, PARAMETROLICENCAPREMIO_ID, INICIOVIGENCIA, FINALVIGENCIA, PERIODOAQUISITIVOFL_ID)
values (HIBERNATE_SEQUENCE.nextval, sysdate, 657790152, to_date('01/01/2015', 'dd/MM/yyyy'), to_date('31/03/2015', 'dd/MM/yyyy'),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638907952
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')));

insert into concessaoferiaslicenca(ID, DATAINICIAL, DATAFINAL, DATACADASTRO, PROGRAMACAOLICENCAPREMIO_ID, PERIODOAQUISITIVOFL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2018', 'dd/MM/yyyy'), to_date('02/03/2018', 'dd/MM/yyyy'), sysdate,
        (select prog.id
         from ProgramacaoLicencaPremio prog
                  inner JOIN periodoAquisitivoFL periodo on prog.PERIODOAQUISITIVOFL_ID = periodo.ID
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638907952
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')),
        (select periodo.id
         from periodoAquisitivoFL periodo
                  inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                  inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                  inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
         where v.id = 638907952
           and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
           and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
           and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy')));

update periodoAquisitivoFL
set STATUS          = 'PARCIAL',
    DATAATUALIZACAO = sysdate,
    SALDO           = 30
where ID = (select periodo.id
            from periodoAquisitivoFL periodo
                     inner join BASECARGO bc on periodo.BASECARGO_ID = bc.ID
                     inner join BASEPERIODOAQUISITIVO base on bc.BASEPERIODOAQUISITIVO_ID = base.ID
                     inner join vinculofp v on periodo.CONTRATOFP_ID = v.ID
            where v.id = 638907952
              and base.TIPOPERIODOAQUISITIVO = 'LICENCA'
              and periodo.INICIOVIGENCIA = to_date('01/01/2010', 'dd/MM/yyyy')
              and periodo.FINALVIGENCIA = to_date('31/12/2014', 'dd/MM/yyyy'));

insert into SituacaoContratoFP (id, INICIOVIGENCIA, FINALVIGENCIA, CONTRATOFP_ID, SITUACAOFUNCIONAL_ID)
VALUES (HIBERNATE_SEQUENCE.nextval, to_date('02/01/2018', 'dd/MM/yyyy'), to_date('02/03/2018', 'dd/MM/yyyy'),
        638907952, 6189980);

update SituacaoContratoFP
set INICIOVIGENCIA = to_date('05/03/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('03/04/2018', 'dd/MM/yyyy')
where id = (select sit.id
            from SituacaoContratoFP sit
            where sit.CONTRATOFP_ID = 638907952
              and SITUACAOFUNCIONAL_ID = 6189973
              and sit.INICIOVIGENCIA = to_date('01/02/2018', 'dd/MM/yyyy')
              and sit.FINALVIGENCIA = to_date('02/03/2018', 'dd/MM/yyyy'));
