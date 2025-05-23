UPDATE OUTORGAIPO
SET datainicial =
  (SELECT to_date('01/'
    ||dados.mes
    ||'/'
    ||dados.exercicio, 'dd/MM/yyyy') data_inicial
  FROM
    (SELECT filha.id,
      CASE mes
        WHEN 'JANEIRO'        THEN '01'
        WHEN 'FEVEREIRO'      THEN '02'
        WHEN 'MARCO'          THEN '03'
        WHEN 'ABRIL'          THEN '04'
        WHEN 'MAIO'           THEN '05'
        WHEN 'JUNHO'          THEN '06'
        WHEN 'JULHO'          THEN '07'
        WHEN 'AGOSTO'         THEN '08'
        WHEN 'SETEMBRO'       THEN '09'
        WHEN 'OUTUBRO'        THEN '10'
        WHEN 'NOVEMBRO'       THEN '11'
        WHEN 'DEZEMBRO'       THEN '12'
      END mes,
      (SELECT e.ano FROM ContribuinteDebitoOutorga pai
        INNER JOIN exercicio e ON pai.exercicio_id = e.id
        WHERE pai.id        = filha.CONTRIBUINTEDEBITOOUTORGA_ID
      ) exercicio
    FROM OUTORGAIPO filha
    ) dados
  WHERE dados.id = OUTORGAIPO.id
  );

UPDATE OUTORGAIPO SET datafinal = last_day(datainicial);
