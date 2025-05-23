delete CONCESSAOFERIASLICENCA where id = (select concessao.id from CONCESSAOFERIASLICENCA concessao
inner join PERIODOAQUISITIVOFL pa on concessao.PERIODOAQUISITIVOFL_ID = pa.id
where pa.CONTRATOFP_ID = 639004718 and concessao.DATAINICIAL = to_date('03/02/2021', 'dd/MM/yyyy'));
