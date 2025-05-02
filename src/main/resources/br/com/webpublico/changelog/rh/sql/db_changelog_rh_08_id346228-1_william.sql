update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2017', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2018', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2017', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2018', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2019', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2018', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2019', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2020', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2019', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2020', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2021', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2020', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2021', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2022', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2021', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2022', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2023', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2022', 'dd/MM/yyyy'));

update PERIODOAQUISITIVOFL
set INICIOVIGENCIA = to_date('02/05/2023', 'dd/MM/yyyy'),
    FINALVIGENCIA  = to_date('01/05/2024', 'dd/MM/yyyy')
where id = (select pa.id
            from PERIODOAQUISITIVOFL pa
                     inner join contratofp contrato on pa.CONTRATOFP_ID = contrato.ID
            where contrato.id = 638937918
              and INICIOVIGENCIA = to_date('31/12/2023', 'dd/MM/yyyy'));
