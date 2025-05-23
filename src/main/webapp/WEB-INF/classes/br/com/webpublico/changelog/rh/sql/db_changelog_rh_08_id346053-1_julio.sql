update periodoaquisitivofl periodoaq
set finalvigencia  = to_date('29/02/2024', 'dd/MM/yyyy'),
    quantidadedias = 366
where periodoaq.contratofp_id = 10922025680
  and periodoaq.iniciovigencia = to_date('01/03/2023', 'dd/MM/yyyy')
  and periodoaq.finalvigencia = to_date('28/02/2024', 'dd/MM/yyyy');
