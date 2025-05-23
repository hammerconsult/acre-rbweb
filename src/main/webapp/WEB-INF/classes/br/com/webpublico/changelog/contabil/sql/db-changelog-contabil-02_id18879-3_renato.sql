update contratoobn set numeroHeaderObn600 = (SELECT numeroHeaderObn600 from bancoobn where id = bancoObn_id)
